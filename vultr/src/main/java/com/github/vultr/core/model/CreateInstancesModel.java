package com.github.vultr.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateInstancesModel {
    private String region;
    private String plan;
    private String snapshot_id;
    private String backups;



//    	"region": "sjc",
//                "plan": "vc2-1c-1gb",
//                "label": "ss Instance",
//                "snapshot_id": "ecf0eeb7-5b73-4e01-93e5-7af0e46a9a84",
//                "user_data": "QmFzZTY0IEV4YW1wbGUgRGF0YQ==",
//                "backups": "disabled",
//                "tags": [
//                "ss"
//                ]

}
