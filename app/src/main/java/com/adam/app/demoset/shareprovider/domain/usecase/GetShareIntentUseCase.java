/*
 * Copyright (c) 2026 Adam Chen
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

package com.adam.app.demoset.shareprovider.domain.usecase;

import android.content.Intent;
import com.adam.app.demoset.shareprovider.domain.model.ShareContent;
import com.adam.app.demoset.utils.DemoAppConstants;
import javax.inject.Inject;

/**
 * Use case to generate a share intent based on the content type.
 */
public class GetShareIntentUseCase {

    @Inject
    public GetShareIntentUseCase() {
    }

    /**
     * Executes the use case to get a share intent.
     *
     * @param content The content to share.
     * @return The configured share intent.
     */
    public Intent execute(ShareContent content) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (content.isImage()) {
            shareIntent.setType(DemoAppConstants.MIME_TYPE_IMAGE);
            shareIntent.putExtra(Intent.EXTRA_STREAM, content.getImageUri());
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            shareIntent.setType(DemoAppConstants.MIME_TYPE_TEXT);
            shareIntent.putExtra(Intent.EXTRA_TEXT, content.getText());
        }
        return shareIntent;
    }
}
