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
import com.adam.app.demoset.encryption.database.data.dao.FullEncryptionDao;
import com.adam.app.demoset.encryption.database.data.model.FullEncryptionItem;
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {FullEncryptionItem.class}, version = 1, exportSchema = false)
public abstract class FullEncryptionDatabase extends RoomDatabase {

    public abstract FullEncryptionDao fullEncryptionDao();

    private static volatile FullEncryptionDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static FullEncryptionDatabase getDatabase(final Context context, byte[] passphrase) {
        if (INSTANCE == null) {
            synchronized (FullEncryptionDatabase.class) {
                if (INSTANCE == null) {
                    // SQLCipher requires loading native libraries
                    System.loadLibrary("sqlcipher");

                    SupportOpenHelperFactory factory = new SupportOpenHelperFactory(passphrase);
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    FullEncryptionDatabase.class, "full_encryption_database")
                            .openHelperFactory(factory)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}


