package com.cdbwsoft.library.ble;

import java.util.UUID;

/**
 * 定义字段
 * Created by DDL on 2016/5/17.
 */
public class BleGlobalVariables {
	public static final String QuinticOtaService              = "0000fee8-0000-1000-8000-00805f9b34fb";
	public static final String QuinticQppService              = "0000fee9-0000-1000-8000-00805f9b34fb";
	public static final String OtaWriteCharacteristic         = "013784cf-f7e3-55b4-6c4c-9fd140100a16";
	public static final String OtaNotifyCharacteristic        = "003784cf-f7e3-55b4-6c4c-9fd140100a16";
	public static final String QppWriteCharacteristic         = "d44bc439-abfd-45a2-b575-925416129600";
	public static final String QppDescriptor                  = "00002902-0000-1000-8000-00805f9b34fb";
	public static final UUID   UUID_QUINTIC_OTA_SERVICE       = UUID.fromString(QuinticOtaService);
	public static final UUID   UUID_QUINTIC_QPP_SERVICE       = UUID.fromString(QuinticQppService);
	public static final UUID   UUID_OTA_WRITE_CHARACTERISTIC  = UUID.fromString(OtaWriteCharacteristic);
	public static final UUID   UUID_OTA_NOTIFY_CHARACTERISTIC = UUID.fromString(OtaNotifyCharacteristic);
	public static final UUID   UUID_QPP_WRITE_CHARACTERISTIC  = UUID.fromString(QppWriteCharacteristic);
	public static final UUID   UUID_QPP_DESCRIPTOR            = UUID.fromString(QppDescriptor);

	protected enum OtaNotiDataPkg {
		OTA_NOTI_LENGTH_L,
		OTA_NOTI_LENGTH_H,
		OTA_NOTI_CMD,
		OTA_NOTI_RESULT,
		OTA_NOTI_RCVED_LENGTH_L,
		OTA_NOTI_RCVED_LENGTH_H,
		OTA_NOTI_RCVED_CS_L,
		OTA_NOTI_RCVED_CS_H;
	}

	protected enum OtaCmd {
		OTA_CMD_META_DATA,
		OTA_CMD_BRICK_DATA,
		OTA_CMD_DATA_VERIFY,
		OTA_CMD_EXECUTION_NEW_CODE
	}

	public enum OtaResult {
		OTA_RESULT_SUCCESS,
		OTA_RESULT_PKT_CHECKSUM_ERROR,
		OTA_RESULT_PKT_LEN_ERROR,
		OTA_RESULT_DEVICE_NOT_SUPPORT_OTA,
		OTA_RESULT_FW_SIZE_ERROR,
		OTA_RESULT_FW_VERIFY_ERROR,
		OTA_RESULT_INVALID_ARGUMENT,
		OTA_RESULT_OPEN_FIRMWAREFILE_ERROR,
		OTA_RESULT_SEND_META_ERROR,
		OTA_RESULT_RECEIVED_INVALID_PACKET,
		OTA_RESULT_META_RESPONSE_TIMEOUT,
		OTA_RESULT_DATA_RESPONSE_TIMEOUT;
	}
}
