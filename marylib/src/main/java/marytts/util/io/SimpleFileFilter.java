/**
 * Copyright 2000-2006 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * This file is part of MARY TTS.
 * <p/>
 * MARY TTS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package marytts.util.io;

import java.io.File;

import marytts.util.MaryUtils;

public class SimpleFileFilter extends FileFilter {

    private String extension;
    private String description;

    public SimpleFileFilter(String extension) {
        super(extension);
        this.extension = extension;
    }

    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String ext = MaryUtils.getExtension(f);
        if (ext != null) {
            return ext.equals(extension);
        }
        return false;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public String getExtension() {
        return extension;
    }
}

