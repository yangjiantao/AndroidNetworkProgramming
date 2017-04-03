package com.medlinker.socketclient.aidl;

// 空实现，用于绑定前台activity，提高进程优先级，防止被杀。
interface ImAIDLService{
    boolean isSocketConnected();
}