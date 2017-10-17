package com.cdbwsoft.library.vo;

import java.util.Date;

/**
 * OTA更新VO
 * Created by DDL on 2016/3/8.
 */
public class OtaVO {
	public long   app_id;
	public String ota_version;
	public int    ota_version_number;
	public String ota_file;
	public float  ota_size;
	public boolean force;
	public int min_version;
	public int hardware;
	public String ota_update;
	public Date   ota_modified;
}
