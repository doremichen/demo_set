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
    @ColumnInfo(name = "id")
    private int mId;

    @ColumnInfo(name = "alias")
    private String mAlias;

    @ColumnInfo(name = "encrypted_data")
    private String mEncryptedData;

    @ColumnInfo(name = "iv")
    private String mIv;

    @Ignore
    private String mDecryptedData;

    public FieldLevelEncryptionItem(String alias, String encryptedData, String iv) {
        this.mAlias = alias;
        this.mEncryptedData = encryptedData;
        this.mIv = iv;
    }

    public int getId() { return mId; }
    public void setId(int id) { this.mId = id; }
    public String getAlias() { return mAlias; }
    public void setAlias(String alias) { this.mAlias = alias; }
    public String getEncryptedData() { return mEncryptedData; }
    public void setEncryptedData(String encryptedData) { this.mEncryptedData = encryptedData; }
    public String getIv() { return mIv; }
    public void setIv(String iv) { this.mIv = iv; }
    public String getDecryptedData() { return mDecryptedData; }
    public void setDecryptedData(String decryptedData) { this.mDecryptedData = decryptedData; }
}
