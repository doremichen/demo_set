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
package com.adam.app.demoset.encryption.database.data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.adam.app.demoset.encryption.database.data.dao.FullDbEncryptionDao;
import com.adam.app.demoset.encryption.database.data.model.FullDbEncryptionItem;
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {FullDbEncryptionItem.class}, version = 1, exportSchema = false)
public abstract class FullDbEncryptionDatabase extends RoomDatabase {

    public abstract FullDbEncryptionDao fullDbEncryptionDao();

    private static volatile FullDbEncryptionDatabase sInstance;
    public static final ExecutorService sDatabaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static FullDbEncryptionDatabase getDatabase(final Context context, byte[] passphrase) {
        if (sInstance == null) {
            synchronized (FullDbEncryptionDatabase.class) {
                if (sInstance == null) {
                    // SQLCipher requires loading native libraries
                    System.loadLibrary("sqlcipher");

                    SupportOpenHelperFactory factory = new SupportOpenHelperFactory(passphrase);
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                                    FullDbEncryptionDatabase.class, "full_encryption_database")
                            .openHelperFactory(factory)
                            .build();
                }
            }
        }
        return sInstance;
    }
}


