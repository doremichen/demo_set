/*
 * Copyright (c) 2024 Adam Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.adam.app.demoset.encryption.database.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "encryption_items")
public class FieldLevelEncryptionItem {

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

    public FieldLevelEncryptionItem(String alias, String encryptedData, String iv) {
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


