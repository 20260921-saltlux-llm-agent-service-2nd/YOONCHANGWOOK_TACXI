package app.model.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class TG_TableDefDto {
    private int genMntrsqno;
    private int memberSqno;
    private String tableName;
    private String colDataType;
    private String columnNm;
    private String columSize;
    private String nullYn;
    private String pkYn;
    private String fkYn;
    private String refTableNm;
    private String refColumn;
    private String defaultStr;
    private String comment;
    private String lschgDtm;
}
