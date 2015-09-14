/*
Copyright (C) 2008-2009  Santiago Carot Nemesio
email: scarot@libresoft.es

This program is a (FLOS) free libre and open source implementation
of a multiplatform manager device written in java according to the
ISO/IEEE 11073-20601. Manager application is designed to work in
DalvikVM over android platform.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

package middleHealth.ieee_11073.part_20601.asn1;
//
// This file was generated by the BinaryNotes compiler.
// See http://bnotes.sourceforge.net
// Any modifications to this file will be lost upon recompilation of the source ASN.1.
//

import middleHealth.org.bn.*;
import middleHealth.org.bn.annotations.*;
import middleHealth.org.bn.coders.*;


@ASN1PreparedElement
    @ASN1BoxedType ( name = "SegmEntryElemList" )
    public class SegmEntryElemList implements IASN1PreparedElement {


            @ASN1SequenceOf( name = "SegmEntryElemList" , isSetOf = false)
	    private java.util.Collection<SegmEntryElem> value = null;

            public SegmEntryElemList () {
            }

            public SegmEntryElemList ( java.util.Collection<SegmEntryElem> value ) {
                setValue(value);
            }

            public void setValue(java.util.Collection<SegmEntryElem> value) {
                this.value = value;
            }

            public java.util.Collection<SegmEntryElem> getValue() {
                return this.value;
            }

            public void initValue() {
                setValue(new java.util.LinkedList<SegmEntryElem>());
            }

            public void add(SegmEntryElem item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SegmEntryElemList.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
