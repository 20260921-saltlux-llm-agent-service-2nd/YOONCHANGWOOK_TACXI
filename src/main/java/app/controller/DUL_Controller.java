package app.controller;

import app.model.DTO.DL_SelectColnmDTO;
import app.repository.DL_Repository;
import app.repository.DUL_Repository;
import app.view.DL_view;

import java.util.List;

public class DUL_Controller {


    private final DL_Repository dl_repository;
    private final DUL_Repository dul_repository;
    DL_view DdlView = new DL_view();

    public DUL_Controller() {
        dl_repository = new DL_Repository();
        dul_repository = new DUL_Repository();
    }

    public boolean StartDUL_Controller(String table_nm,  String targetPath) {

        // 테이블명 조회
        List<DL_SelectColnmDTO> ColnmDTOList = dl_repository.selectColnmDTOList(table_nm);

        if (ColnmDTOList.isEmpty()) {
            DdlView.displayMessage("존재하지 않는 테이블입니다: " + table_nm);
            return false;
        }

        boolean result = dul_repository.DataUnload(ColnmDTOList, table_nm, targetPath);
        if(result) {
            DdlView.displayMessage("UNLOAD가 성공적으로 완료되었습니다.");
        } else {
            DdlView.displayMessage("UNLOAD가 실패하였습니다.");

        };

        return result;
    }

}
