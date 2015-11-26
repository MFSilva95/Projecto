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

package pt.it.porto.mydiabetes.middleHealth.ieee_11073.part_20601.asn1;
//
// This file was generated by the BinaryNotes compiler.
// See http://bnotes.sourceforge.net
// Any modifications to this file will be lost upon recompilation of the source ASN.1.
//

import pt.it.porto.mydiabetes.middleHealth.org.bn.CoderFactory;
import pt.it.porto.mydiabetes.middleHealth.org.bn.annotations.ASN1Element;
import pt.it.porto.mydiabetes.middleHealth.org.bn.annotations.ASN1Integer;
import pt.it.porto.mydiabetes.middleHealth.org.bn.annotations.ASN1PreparedElement;
import pt.it.porto.mydiabetes.middleHealth.org.bn.annotations.ASN1Sequence;
import pt.it.porto.mydiabetes.middleHealth.org.bn.annotations.constraints.ASN1ValueRangeConstraint;
import pt.it.porto.mydiabetes.middleHealth.org.bn.coders.IASN1PreparedElement;
import pt.it.porto.mydiabetes.middleHealth.org.bn.coders.IASN1PreparedElementData;


@ASN1PreparedElement
    @ASN1Sequence ( name = "MetricStructureSmall", isSet = false )
    public class MetricStructureSmall implements IASN1PreparedElement {
            @ASN1Integer( name = "" )
            @ASN1ValueRangeConstraint (

		min = 0L,

		max = 255L

	   )
        @ASN1Element ( name = "ms-struct", isOptional =  false , hasTag =  false  , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 0  )

	private Integer ms_struct = null;

  @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint (

		min = 0L,

		max = 255L

	   )

        @ASN1Element ( name = "ms-comp-no", isOptional =  false , hasTag =  false  , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 1  )

	private Integer ms_comp_no = null;



        public Integer getMs_struct () {
            return this.ms_struct;
        }



        public void setMs_struct (Integer value) {
            this.ms_struct = value;
        }



        public Integer getMs_comp_no () {
            return this.ms_comp_no;
        }



        public void setMs_comp_no (Integer value) {
            this.ms_comp_no = value;
        }




        public void initWithDefaults() {

        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(MetricStructureSmall.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
