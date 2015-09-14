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
import middleHealth.org.bn.annotations.constraints.*;
import middleHealth.org.bn.coders.*;


@ASN1PreparedElement
    @ASN1Sequence ( name = "AttrValMapEntry", isSet = false )
    public class AttrValMapEntry implements IASN1PreparedElement {

        @ASN1Element ( name = "attribute-id", isOptional =  false , hasTag =  false  , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 0  )

	private OID_Type attribute_id = null;

  @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint (

		min = 0L,

		max = 65535L

	   )

        @ASN1Element ( name = "attribute-len", isOptional =  false , hasTag =  false  , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 1  )

	private Integer attribute_len = null;



        public OID_Type getAttribute_id () {
            return this.attribute_id;
        }



        public void setAttribute_id (OID_Type value) {
            this.attribute_id = value;
        }



        public Integer getAttribute_len () {
            return this.attribute_len;
        }



        public void setAttribute_len (Integer value) {
            this.attribute_len = value;
        }




        public void initWithDefaults() {

        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(AttrValMapEntry.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
