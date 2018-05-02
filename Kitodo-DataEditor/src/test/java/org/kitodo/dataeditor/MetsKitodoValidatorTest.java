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

package org.kitodo.dataeditor;

import java.net.URI;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;
import org.kitodo.dataformat.metskitodo.Mets;

public class MetsKitodoValidatorTest {

    @Test
    public void shouldCheckValidMetsObject() throws JAXBException {
        Mets mets = MetsKitodoReader.readUriToMets(URI.create("./src/test/resources/testmeta.xml"));
        Assert.assertTrue("Result of validation of Mets object was not true!",MetsKitodoValidator.checkMetsKitodoFormatOfMets(mets));
    }

    @Test
    public void shouldCheckOldFormatMetsObject() throws JAXBException {
        Mets mets = MetsKitodoReader.readUriToMets(URI.create("./src/test/resources/testmetaOldFormat.xml"));
        Assert.assertFalse("Result of validation of Mets object was not false!",MetsKitodoValidator.checkMetsKitodoFormatOfMets(mets));
    }
}
