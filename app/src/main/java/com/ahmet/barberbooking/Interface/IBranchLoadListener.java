package com.ahmet.barberbooking.Interface;

import java.util.List;

public interface IBranchLoadListener {

    void onLoadAllSalonSuccess(List<String> mListBranch);
    void onLoadAllSalonFailed(String error);

}
