package com.cdbwsoft.library.net;

import com.cdbwsoft.library.AppConfig;

public interface ErrorStatus {

	public int CLIENT_PROTOCOL_ERROR = 508;
	public int IO_ERROR              = 510;
	public int JSON_PARSE_ERROR      = 511;
	public int OK                    = AppConfig.RESPONSE_SUCCESS_CODE;
	public int REQUEST_FAILED        = 512;
	public int UNSUPPORTED_ENCODING  = 509;
	public int NETWORK_ERROR         = -5;
	public int UNKNOWN_ERROR         = -1;
}
