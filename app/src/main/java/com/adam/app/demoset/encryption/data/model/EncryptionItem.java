/*
 * Copyright (c) 2024 Adam Chen
 */

package com.adam.app.demoset.encryption.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "encryption_items")
public class EncryptionItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "alias")
    private String alias;

    @ColumnInfo(name = "encrypted_data")
    private String encryptedData;

    @ColumnInfo(name = "iv")
    private String iv;

    @Ignore
    private String decryptedData;

    public EncryptionItem(String alias, String encryptedData, String iv) {
        this.alias = alias;
        this.encryptedData = encryptedData;
        this.iv = iv;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
    public String getEncryptedData() { return encryptedData; }
    public void setEncryptedData(String encryptedData) { this.encryptedData = encryptedData; }
    public String getIv() { return iv; }
    public void setIv(String iv) { this.iv = iv; }
    public String getDecryptedData() { return decryptedData; }
    public void setDecryptedData(String decryptedData) { this.decryptedData = decryptedData; }
}
