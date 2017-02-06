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
package org.goobi.production.plugin.CataloguePlugin.PicaPlugin;

import org.apache.commons.configuration.HierarchicalConfiguration;

/**
 * Map rules are used to map data from norm data records to a media record. This
 * is used inside a {@code ResolveRule}. Example usage:
 * 
 * <pre>
 * &lt;catalogue title="…">
 *     &lt;!-- ... -->
 *     &lt;resolve tag="028C" subtag="9" searchField="12">
 *         &lt;map tag="003U" subtag="a" asSubtag="g" />
 *     &lt;/resolve>
 * </pre>
 * 
 * In this case, the value from PICA field 028C subfield 9 is taken to execute a
 * catalogue query on search field 12. From the result of this query, the value
 * from field 003U subfield a is taken and stored in subfield g on the instance
 * of field 028C of the main record.
 * 
 * @author Matthias Ronge
 */
class MapRule {
	/**
	 * Creates a new resolve rule from the XML configuration. The attributes
	 * {@code tag}, {@code subtag} and {@code asSubtag} are required.
	 * 
	 * @param config
	 *            configuration for the resolve rule from
	 */
	public MapRule(HierarchicalConfiguration config) {
		tag = config.getString("tag");
		subtag = config.getString("subtag");
		asSubtag = config.getString("asSubtag");
	}

	/**
	 * PICA field tag of a field that has a subfield that shall be imported from
	 * the resolved record. Typical values match the pattern
	 * <code>\d{3}[@-Z]</code>. An example value is "003U" for the field holding
	 * a subfield with the GND record path.
	 */
	String tag;

	/**
	 * PICA subfield code of the subfield that shall be imported from the
	 * resolved record. Typical values match the pattern <code>[0-9a-z]</code>.
	 * An example value is "a" for the subfield with the GND record path.
	 */
	String subtag;

	/**
	 * PICA subfield code of a subfield in the original record that the value
	 * identified by tag and subtag shall be written to. The subfield code can
	 * be fictions. Typical values match the pattern <code>[0-9a-z]</code>. An
	 * example value is "g".
	 */
	String asSubtag;
}
