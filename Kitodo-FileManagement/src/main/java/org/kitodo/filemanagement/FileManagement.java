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

package org.kitodo.filemanagement;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;
import org.kitodo.api.filemanagement.FileManagementInterface;
import org.kitodo.api.filemanagement.ProcessLocation;
import org.kitodo.config.Config;

public class FileManagement implements FileManagementInterface {

    private static final String METADATA_DIRECTORY = "MetadatenVerzeichnis";

    @Override
    public OutputStream write(URI uri) throws IOException {
        URL url = uri.toURL();
        URLConnection urlConnection = url.openConnection();
        return urlConnection.getOutputStream();
    }

    @Override
    public InputStream read(URI uri) throws IOException {
        URL url = uri.toURL();
        return url.openStream();
    }

    @Override
    public boolean delete(URI uri) throws IOException {
        File file = new File(uri);
        if (file.isFile()) {
            return file.delete();
        }
        if (file.isDirectory()) {
            FileUtils.deleteDirectory(file);
            return true;
        }
        return false;
    }

    @Override
    public ProcessLocation createProcessLocation(String processId) throws IOException {
        File processRootDirectory = new File(
                (Config.getConfig().getString(METADATA_DIRECTORY) + File.separator + processId));
        if (!processRootDirectory.mkdir()) {
            throw new IOException("Could not create processRoot directory.");
        }

        File processImageDirectory = new File(processRootDirectory.getPath() + File.separator + "images");
        if (!processImageDirectory.mkdir()) {
            throw new IOException("Could not create image directory");
        }
        URI processMetaFile = createResource(processRootDirectory.toURI(), "meta.xml");

        return new ProcessLocation(processRootDirectory.toURI(), processImageDirectory.toURI(), processMetaFile);

    }

    @Override
    public URI createDirectory(URI parentFolderUri, String directoryName) throws IOException {
        File directory = new File(parentFolderUri.getPath() + File.separator + directoryName);
        if (!directory.mkdir()) {
            throw new IOException("Could not create directory.");
        }
        return directory.toURI();
    }

    @Override
    public URI createResource(URI parentFolderUri, String fileName) {
        return new File(parentFolderUri.getPath() + File.separator + fileName).toURI();
    }
}