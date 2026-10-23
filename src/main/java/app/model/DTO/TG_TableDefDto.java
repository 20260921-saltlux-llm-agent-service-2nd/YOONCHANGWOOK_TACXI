package app.model.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class TG_TableDefDto {
    private int gen_Mntr_sqno;
    private int memberSqno;
    private String tableName;
    private String colDataType;
    private String columnNm;
    private String columnSize;
    private String nullYn;
    private String pkYn;
    private String fkYn;
    private String refTableNm;
    private String refColNm;
    private String defaultStr;
    private String comment;
}
