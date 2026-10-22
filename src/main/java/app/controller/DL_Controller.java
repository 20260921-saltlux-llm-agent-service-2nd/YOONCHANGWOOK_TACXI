package app.controller;

import app.model.DTO.DL_SelectColnmDTO;
import app.repository.DL_Repository;
import app.view.DL_view;

import java.util.ArrayList;
import java.util.List;

public class DL_Controller {


    private final DL_Repository dl_repository;
    DL_view DdlView = new DL_view();



    public DL_Controller() {
        dl_repository = new DL_Repository();
    }

    public boolean StartDL_Controller (String table_nm, String targetPath) {

        List<DL_SelectColnmDTO> ColnmDTOList = dl_repository.selectColnmDTOList(table_nm);
        if (ColnmDTOList.isEmpty()) {
            DdlView.displayMessage("존재하지 않는 테이블입니다: " + table_nm);
            return false;
        }

        boolean result = DL_Start(ColnmDTOList,table_nm,targetPath);

        if(result) {
            DdlView.displayMessage("LOAD가 성공적으로 완료었습니다.");
        } else {
            DdlView.displayMessage("LOAD가 실패하였습니다.");

        };
        return result;

    }
    public boolean DL_Start(List<DL_SelectColnmDTO> ColnmDTOList,String table_nm, String targetPath) {
        boolean result = dl_repository.Loadcsv(ColnmDTOList,table_nm,targetPath);

        return result;
    }


}
