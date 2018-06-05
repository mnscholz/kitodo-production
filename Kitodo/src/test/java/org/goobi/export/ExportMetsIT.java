/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.goobi.export;

import de.sub.goobi.export.download.ExportMets;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang.SystemUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kitodo.FileLoader;
import org.kitodo.MockDatabase;
import org.kitodo.SecurityTestUtils;
import org.kitodo.config.Config;
import org.kitodo.data.database.beans.Process;
import org.kitodo.data.database.beans.User;
import org.kitodo.services.ServiceManager;
import org.kitodo.services.file.FileService;

public class ExportMetsIT {

    private static ServiceManager serviceManager = new ServiceManager();
    private static FileService fileService = serviceManager.getFileService();
    private static String userDirectory;
    private static String metadataDirectory;
    private static URI exportUri;
    private static Process process;

    private ExportMets exportMets = new ExportMets();

    @BeforeClass
    public static void setUp() throws Exception {
        MockDatabase.startNode();
        MockDatabase.insertProcessesFull();

        User user = serviceManager.getUserService().getById(1);
        process = serviceManager.getProcessService().getById(1);
        metadataDirectory = process.getId().toString();
        userDirectory = user.getLogin();
        exportUri = Paths.get(Config.getParameter("dir_Users") + userDirectory).toUri();

        fileService.createDirectory(URI.create(""), metadataDirectory);
        fileService.copyFileToDirectory(URI.create("metadata/testmetaOldFormat.xml"), URI.create(metadataDirectory));
        fileService.renameFile(URI.create(metadataDirectory + "/testmetaOldFormat.xml"), "meta.xml");
        SecurityTestUtils.addUserDataToSecurityContext(user);
        FileLoader.createConfigProjectsFile();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        SecurityTestUtils.cleanSecurityContext();
        MockDatabase.stopNode();
        MockDatabase.cleanDatabase();
        fileService.delete(URI.create(metadataDirectory));
        fileService.delete(Paths.get(Config.getParameter("dir_Users")).toUri());
        FileLoader.deleteConfigProjectsFile();
    }

    @Test
    public void exportMetsTest() throws Exception {
        if (SystemUtils.IS_OS_WINDOWS) {
            // This is a workaround for the problem that the startExport method is calling
            // an external shell script for creating directories. This code only does the work of that script.
            //TODO Find a better way for changing script selection corresponding to OS
            File userdataDirectory = new File(Config.getParameter("dir_Users"));
            if (!userdataDirectory.exists() && !userdataDirectory.mkdir()) {
                throw new IOException("Could not create users directory");
            }
            fileService.createDirectory(Paths.get(Config.getParameter("dir_Users")).toUri(), userDirectory);
        }

        exportMets.startExport(process, exportUri);
        List<String> strings = Files.readAllLines(Paths.get(Config.getParameter("dir_Users") + userDirectory + "/"
                + serviceManager.getProcessService().getNormalizedTitle(process.getTitle()) + "_mets.xml"));

        Assert.assertTrue("Export of metadata was wrong", strings.get(1).contains("<mods:publisher>Test Publisher</mods:publisher>"));
        Assert.assertTrue("Export of person was wrong", strings.get(1).contains("<mods:title>Test Title</mods:title>"));
        Assert.assertTrue("Export of metadata group was wrong", strings.get(1).contains("<mods:namePart type=\"given\">FirstTestName</mods:namePart>"));

    }
}
