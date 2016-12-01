package com.android.core.control.crash;

import java.io.File;

/**
 * @作者: liulei
 * @公司：希顿科技
 */
public interface HttpReportCallback {

    void uploadException2remote(File file);
}
