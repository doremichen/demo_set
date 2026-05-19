/*
 * Copyright (c) 2024 Adam Chen
 */

package com.adam.app.demoset.encryption.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "full_encryption_items")
public class FullEncryptionItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "alias")
    private String alias;

    @ColumnInfo(name = "secret_info")
    private String secretInfo;

    public FullEncryptionItem(String alias, String secretInfo) {
        this.alias = alias;
        this.secretInfo = secretInfo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
    public String getSecretInfo() { return secretInfo; }
    public void setSecretInfo(String secretInfo) { this.secretInfo = secretInfo; }
}
