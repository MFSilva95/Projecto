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
    @ASN1Sequence ( name = "GetArgumentSimple", isSet = false )
    public class GetArgumentSimple implements IASN1PreparedElement {

        @ASN1Element ( name = "obj-handle", isOptional =  false , hasTag =  false  , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 0  )

	private HANDLE obj_handle = null;


        @ASN1Element ( name = "attribute-id-list", isOptional =  false , hasTag =  false  , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 1  )

	private AttributeIdList attribute_id_list = null;



        public HANDLE getObj_handle () {
            return this.obj_handle;
        }



        public void setObj_handle (HANDLE value) {
            this.obj_handle = value;
        }



        public AttributeIdList getAttribute_id_list () {
            return this.attribute_id_list;
        }



        public void setAttribute_id_list (AttributeIdList value) {
            this.attribute_id_list = value;
        }




        public void initWithDefaults() {

        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(GetArgumentSimple.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
