package com.masaworld.catmap.data;

import com.masaworld.catmap.R;

public enum Error {

    SERVER_CONNECTION(R.string.error_server_connection),
    LOGIN_FAILED(R.string.error_login_failed),
    UNEXPECTED(R.string.error_unexpected);

    public int messageId;

    Error(int messageId) {
        this.messageId = messageId;
    }
}
