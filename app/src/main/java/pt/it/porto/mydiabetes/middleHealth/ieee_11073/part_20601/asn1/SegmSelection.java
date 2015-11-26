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
import pt.it.porto.mydiabetes.middleHealth.org.bn.annotations.ASN1Choice;
import pt.it.porto.mydiabetes.middleHealth.org.bn.annotations.ASN1Element;
import pt.it.porto.mydiabetes.middleHealth.org.bn.annotations.ASN1PreparedElement;
import pt.it.porto.mydiabetes.middleHealth.org.bn.coders.IASN1PreparedElement;
import pt.it.porto.mydiabetes.middleHealth.org.bn.coders.IASN1PreparedElementData;


@ASN1PreparedElement
    @ASN1Choice ( name = "SegmSelection" )
    public class SegmSelection implements IASN1PreparedElement {

        @ASN1Element ( name = "all-segments", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 0  )

	private INT_U16 all_segments = null;


        @ASN1Element ( name = "segm-id-list", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 1  )

	private SegmIdList segm_id_list = null;


        @ASN1Element ( name = "abs-time-range", isOptional =  false , hasTag =  true, tag = 3 , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 2  )

	private AbsTimeRange abs_time_range = null;



        public INT_U16 getAll_segments () {
            return this.all_segments;
        }

        public boolean isAll_segmentsSelected () {
            return this.all_segments != null;
        }

        private void setAll_segments (INT_U16 value) {
            this.all_segments = value;
        }


        public void selectAll_segments (INT_U16 value) {
            this.all_segments = value;

                    //setAll_segments(null);

                    setSegm_id_list(null);

                    setAbs_time_range(null);

        }




        public SegmIdList getSegm_id_list () {
            return this.segm_id_list;
        }

        public boolean isSegm_id_listSelected () {
            return this.segm_id_list != null;
        }

        private void setSegm_id_list (SegmIdList value) {
            this.segm_id_list = value;
        }


        public void selectSegm_id_list (SegmIdList value) {
            this.segm_id_list = value;

                    setAll_segments(null);

                    //setSegm_id_list(null);

                    setAbs_time_range(null);

        }




        public AbsTimeRange getAbs_time_range () {
            return this.abs_time_range;
        }

        public boolean isAbs_time_rangeSelected () {
            return this.abs_time_range != null;
        }

        private void setAbs_time_range (AbsTimeRange value) {
            this.abs_time_range = value;
        }


        public void selectAbs_time_range (AbsTimeRange value) {
            this.abs_time_range = value;

                    setAll_segments(null);

                    setSegm_id_list(null);

                    //setAbs_time_range(null);

        }




	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SegmSelection.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
