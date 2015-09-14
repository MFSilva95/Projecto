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
    @ASN1Sequence ( name = "MdsTimeInfo", isSet = false )
    public class MdsTimeInfo implements IASN1PreparedElement {

        @ASN1Element ( name = "mds-time-cap-state", isOptional =  false , hasTag =  false  , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 0  )

	private MdsTimeCapState mds_time_cap_state = null;


        @ASN1Element ( name = "time-sync-protocol", isOptional =  false , hasTag =  false  , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 1  )

	private TimeProtocolId time_sync_protocol = null;


        @ASN1Element ( name = "time-sync-accuracy", isOptional =  false , hasTag =  false  , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 2  )

	private RelativeTime time_sync_accuracy = null;

  @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint (

		min = 0L,

		max = 65535L

	   )

        @ASN1Element ( name = "time-resolution-abs-time", isOptional =  false , hasTag =  false  , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 3  )

	private Integer time_resolution_abs_time = null;

  @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint (

		min = 0L,

		max = 65535L

	   )

        @ASN1Element ( name = "time-resolution-rel-time", isOptional =  false , hasTag =  false  , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 4  )

	private Integer time_resolution_rel_time = null;


        @ASN1Element ( name = "time-resolution-high-res-time", isOptional =  false , hasTag =  false  , hasDefaultValue =  false, hasExplicitOrder = true, declarationOrder = 5  )

	private INT_U32 time_resolution_high_res_time = null;



        public MdsTimeCapState getMds_time_cap_state () {
            return this.mds_time_cap_state;
        }



        public void setMds_time_cap_state (MdsTimeCapState value) {
            this.mds_time_cap_state = value;
        }



        public TimeProtocolId getTime_sync_protocol () {
            return this.time_sync_protocol;
        }



        public void setTime_sync_protocol (TimeProtocolId value) {
            this.time_sync_protocol = value;
        }



        public RelativeTime getTime_sync_accuracy () {
            return this.time_sync_accuracy;
        }



        public void setTime_sync_accuracy (RelativeTime value) {
            this.time_sync_accuracy = value;
        }



        public Integer getTime_resolution_abs_time () {
            return this.time_resolution_abs_time;
        }



        public void setTime_resolution_abs_time (Integer value) {
            this.time_resolution_abs_time = value;
        }



        public Integer getTime_resolution_rel_time () {
            return this.time_resolution_rel_time;
        }



        public void setTime_resolution_rel_time (Integer value) {
            this.time_resolution_rel_time = value;
        }



        public INT_U32 getTime_resolution_high_res_time () {
            return this.time_resolution_high_res_time;
        }



        public void setTime_resolution_high_res_time (INT_U32 value) {
            this.time_resolution_high_res_time = value;
        }




        public void initWithDefaults() {

        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(MdsTimeInfo.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
