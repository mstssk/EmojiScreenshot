package jp.mstssk.emojiscreenshot;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.common.base.Strings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MyActivity extends Activity {

    @InjectView(R.id.text)
    TextView text;

    @InjectView(R.id.counter)
    TextView counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_my);
        ButterKnife.inject(this);

        text.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        text.setDrawingCacheEnabled(true);
    }

    @OnClick(R.id.button)
    void start() {
        new Text2PngTask().execute(emojis);
    }


    private class Text2PngTask extends AsyncTask<String, Wrapper, Void> {

        private Object lock = new Object();
        private int count;
        private int sumCount;

        @Override
        protected Void doInBackground(String... params) {
            count = 0;
            sumCount = params.length;
            File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File dir = new File(picDir, "emojis");
            dir.mkdirs();

            for (String param : params) {
                Wrapper wrapper = new Wrapper();
                wrapper.emoji = param;
                publishProgress(wrapper);
                try {
                    // onProgressUpdateのlockを先に取らせたいのでちょびっと待つ
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock) {
                    Bitmap bitmap = wrapper.bitmap;
                    File file = new File(dir, toHexString(param) + ".png");
                    writeFile(file, bitmap);
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Wrapper... values) {
            synchronized (lock) {
                text.setText(values[0].emoji);
                text.buildDrawingCache();
                Bitmap cache = text.getDrawingCache();
                values[0].bitmap = Bitmap.createBitmap(cache);
                text.destroyDrawingCache();
                counter.setText(Integer.toString(++count) + "/" + Integer.toString(sumCount));
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            text.setText("Done!");
        }

        private String toHexString(String str) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                builder.append("_u" + Strings.padStart(Integer.toHexString(str.charAt(i)), 4,
                        '0').toUpperCase());
            }
            return builder.toString();
        }

        private void writeFile(File file, Bitmap bitmap) {
            try {
                FileOutputStream outStream = new FileOutputStream(file);
                bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.close();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

    }

    private static class Wrapper {
        private Bitmap bitmap;
        private String emoji;
    }

    // from http://www.grumdrig.com/emoji-list/
    private static String[] emojis = {"\u00A9", "\u00AE", "\u203C", "\u2049",
            "\u2122", "\u2139", "\u2194", "\u2195", "\u2196", "\u2197", "\u2198",
            "\u2199", "\u21A9", "\u21AA", "\u231A", "\u231B", "\u23E9", "\u23EA",
            "\u23EB", "\u23EC", "\u23F0", "\u23F3", "\u24C2", "\u25AA", "\u25AB",
            "\u25B6", "\u25C0", "\u25FB", "\u25FC", "\u25FD", "\u25FE", "\u2600",
            "\u2601", "\u260E", "\u2611", "\u2614", "\u2615", "\u261D", "\u263A",
            "\u2648", "\u2649", "\u264A", "\u264B", "\u264C", "\u264D", "\u264E",
            "\u264F", "\u2650", "\u2651", "\u2652", "\u2653", "\u2660", "\u2663",
            "\u2665", "\u2666", "\u2668", "\u267B", "\u267F", "\u2693", "\u26A0",
            "\u26A1", "\u26AA", "\u26AB", "\u26BD", "\u26BE", "\u26C4", "\u26C5",
            "\u26CE", "\u26D4", "\u26EA", "\u26F2", "\u26F3", "\u26F5", "\u26FA",
            "\u26FD", "\u2702", "\u2705", "\u2708", "\u2709", "\u270A", "\u270B",
            "\u270C", "\u270F", "\u2712", "\u2714", "\u2716", "\u2728", "\u2733",
            "\u2734", "\u2744", "\u2747", "\u274C", "\u274E", "\u2753", "\u2754",
            "\u2755", "\u2757", "\u2764", "\u2795", "\u2796", "\u2797", "\u27A1",
            "\u27B0", "\u27BF", "\u2934", "\u2935", "\u2B05", "\u2B06", "\u2B07",
            "\u2B1B", "\u2B1C", "\u2B50", "\u2B55", "\u3030", "\u303D", "\u3297",
            "\u3299", "\uD83C\uDC04", "\uD83C\uDCCF", "\uD83C\uDD70", "\uD83C\uDD71",
            "\uD83C\uDD7E", "\uD83C\uDD7F", "\uD83C\uDD8E", "\uD83C\uDD91",
            "\uD83C\uDD92", "\uD83C\uDD93", "\uD83C\uDD94", "\uD83C\uDD95",
            "\uD83C\uDD96", "\uD83C\uDD97", "\uD83C\uDD98", "\uD83C\uDD99",
            "\uD83C\uDD9A", "\uD83C\uDDE8\uD83C\uDDF3", "\uD83C\uDDE9\uD83C\uDDEA",
            "\uD83C\uDDEA\uD83C\uDDF8", "\uD83C\uDDEB\uD83C\uDDF7",
            "\uD83C\uDDEC\uD83C\uDDE7", "\uD83C\uDDEE\uD83C\uDDF9",
            "\uD83C\uDDEF\uD83C\uDDF5", "\uD83C\uDDF0\uD83C\uDDF7",
            "\uD83C\uDDF7\uD83C\uDDFA", "\uD83C\uDDFA\uD83C\uDDF8", "\uD83C\uDE01",
            "\uD83C\uDE02", "\uD83C\uDE1A", "\uD83C\uDE2F", "\uD83C\uDE32",
            "\uD83C\uDE33", "\uD83C\uDE34", "\uD83C\uDE35", "\uD83C\uDE36",
            "\uD83C\uDE37", "\uD83C\uDE38", "\uD83C\uDE39", "\uD83C\uDE3A",
            "\uD83C\uDE50", "\uD83C\uDE51", "\uD83C\uDF00", "\uD83C\uDF01",
            "\uD83C\uDF02", "\uD83C\uDF03", "\uD83C\uDF04", "\uD83C\uDF05",
            "\uD83C\uDF06", "\uD83C\uDF07", "\uD83C\uDF08", "\uD83C\uDF09",
            "\uD83C\uDF0A", "\uD83C\uDF0B", "\uD83C\uDF0C", "\uD83C\uDF0D",
            "\uD83C\uDF0E", "\uD83C\uDF0F", "\uD83C\uDF10", "\uD83C\uDF11",
            "\uD83C\uDF12", "\uD83C\uDF13", "\uD83C\uDF14", "\uD83C\uDF15",
            "\uD83C\uDF16", "\uD83C\uDF17", "\uD83C\uDF18", "\uD83C\uDF19",
            "\uD83C\uDF1A", "\uD83C\uDF1B", "\uD83C\uDF1C", "\uD83C\uDF1D",
            "\uD83C\uDF1E", "\uD83C\uDF1F", "\uD83C\uDF20", "\uD83C\uDF30",
            "\uD83C\uDF31", "\uD83C\uDF32", "\uD83C\uDF33", "\uD83C\uDF34",
            "\uD83C\uDF35", "\uD83C\uDF37", "\uD83C\uDF38", "\uD83C\uDF39",
            "\uD83C\uDF3A", "\uD83C\uDF3B", "\uD83C\uDF3C", "\uD83C\uDF3D",
            "\uD83C\uDF3E", "\uD83C\uDF3F", "\uD83C\uDF40", "\uD83C\uDF41",
            "\uD83C\uDF42", "\uD83C\uDF43", "\uD83C\uDF44", "\uD83C\uDF45",
            "\uD83C\uDF46", "\uD83C\uDF47", "\uD83C\uDF48", "\uD83C\uDF49",
            "\uD83C\uDF4A", "\uD83C\uDF4B", "\uD83C\uDF4C", "\uD83C\uDF4D",
            "\uD83C\uDF4E", "\uD83C\uDF4F", "\uD83C\uDF50", "\uD83C\uDF51",
            "\uD83C\uDF52", "\uD83C\uDF53", "\uD83C\uDF54", "\uD83C\uDF55",
            "\uD83C\uDF56", "\uD83C\uDF57", "\uD83C\uDF58", "\uD83C\uDF59",
            "\uD83C\uDF5A", "\uD83C\uDF5B", "\uD83C\uDF5C", "\uD83C\uDF5D",
            "\uD83C\uDF5E", "\uD83C\uDF5F", "\uD83C\uDF60", "\uD83C\uDF61",
            "\uD83C\uDF62", "\uD83C\uDF63", "\uD83C\uDF64", "\uD83C\uDF65",
            "\uD83C\uDF66", "\uD83C\uDF67", "\uD83C\uDF68", "\uD83C\uDF69",
            "\uD83C\uDF6A", "\uD83C\uDF6B", "\uD83C\uDF6C", "\uD83C\uDF6D",
            "\uD83C\uDF6E", "\uD83C\uDF6F", "\uD83C\uDF70", "\uD83C\uDF71",
            "\uD83C\uDF72", "\uD83C\uDF73", "\uD83C\uDF74", "\uD83C\uDF75",
            "\uD83C\uDF76", "\uD83C\uDF77", "\uD83C\uDF78", "\uD83C\uDF79",
            "\uD83C\uDF7A", "\uD83C\uDF7B", "\uD83C\uDF7C", "\uD83C\uDF80",
            "\uD83C\uDF81", "\uD83C\uDF82", "\uD83C\uDF83", "\uD83C\uDF84",
            "\uD83C\uDF85", "\uD83C\uDF86", "\uD83C\uDF87", "\uD83C\uDF88",
            "\uD83C\uDF89", "\uD83C\uDF8A", "\uD83C\uDF8B", "\uD83C\uDF8C",
            "\uD83C\uDF8D", "\uD83C\uDF8E", "\uD83C\uDF8F", "\uD83C\uDF90",
            "\uD83C\uDF91", "\uD83C\uDF92", "\uD83C\uDF93", "\uD83C\uDFA0",
            "\uD83C\uDFA1", "\uD83C\uDFA2", "\uD83C\uDFA3", "\uD83C\uDFA4",
            "\uD83C\uDFA5", "\uD83C\uDFA6", "\uD83C\uDFA7", "\uD83C\uDFA8",
            "\uD83C\uDFA9", "\uD83C\uDFAA", "\uD83C\uDFAB", "\uD83C\uDFAC",
            "\uD83C\uDFAD", "\uD83C\uDFAE", "\uD83C\uDFAF", "\uD83C\uDFB0",
            "\uD83C\uDFB1", "\uD83C\uDFB2", "\uD83C\uDFB3", "\uD83C\uDFB4",
            "\uD83C\uDFB5", "\uD83C\uDFB6", "\uD83C\uDFB7", "\uD83C\uDFB8",
            "\uD83C\uDFB9", "\uD83C\uDFBA", "\uD83C\uDFBB", "\uD83C\uDFBC",
            "\uD83C\uDFBD", "\uD83C\uDFBE", "\uD83C\uDFBF", "\uD83C\uDFC0",
            "\uD83C\uDFC1", "\uD83C\uDFC2", "\uD83C\uDFC3", "\uD83C\uDFC4",
            "\uD83C\uDFC6", "\uD83C\uDFC7", "\uD83C\uDFC8", "\uD83C\uDFC9",
            "\uD83C\uDFCA", "\uD83C\uDFE0", "\uD83C\uDFE1", "\uD83C\uDFE2",
            "\uD83C\uDFE3", "\uD83C\uDFE4", "\uD83C\uDFE5", "\uD83C\uDFE6",
            "\uD83C\uDFE7", "\uD83C\uDFE8", "\uD83C\uDFE9", "\uD83C\uDFEA",
            "\uD83C\uDFEB", "\uD83C\uDFEC", "\uD83C\uDFED", "\uD83C\uDFEE",
            "\uD83C\uDFEF", "\uD83C\uDFF0", "\uD83D\uDC00", "\uD83D\uDC01",
            "\uD83D\uDC02", "\uD83D\uDC03", "\uD83D\uDC04", "\uD83D\uDC05",
            "\uD83D\uDC06", "\uD83D\uDC07", "\uD83D\uDC08", "\uD83D\uDC09",
            "\uD83D\uDC0A", "\uD83D\uDC0B", "\uD83D\uDC0C", "\uD83D\uDC0D",
            "\uD83D\uDC0E", "\uD83D\uDC0F", "\uD83D\uDC10", "\uD83D\uDC11",
            "\uD83D\uDC12", "\uD83D\uDC13", "\uD83D\uDC14", "\uD83D\uDC15",
            "\uD83D\uDC16", "\uD83D\uDC17", "\uD83D\uDC18", "\uD83D\uDC19",
            "\uD83D\uDC1A", "\uD83D\uDC1B", "\uD83D\uDC1C", "\uD83D\uDC1D",
            "\uD83D\uDC1E", "\uD83D\uDC1F", "\uD83D\uDC20", "\uD83D\uDC21",
            "\uD83D\uDC22", "\uD83D\uDC23", "\uD83D\uDC24", "\uD83D\uDC25",
            "\uD83D\uDC26", "\uD83D\uDC27", "\uD83D\uDC28", "\uD83D\uDC29",
            "\uD83D\uDC2A", "\uD83D\uDC2B", "\uD83D\uDC2C", "\uD83D\uDC2D",
            "\uD83D\uDC2E", "\uD83D\uDC2F", "\uD83D\uDC30", "\uD83D\uDC31",
            "\uD83D\uDC32", "\uD83D\uDC33", "\uD83D\uDC34", "\uD83D\uDC35",
            "\uD83D\uDC36", "\uD83D\uDC37", "\uD83D\uDC38", "\uD83D\uDC39",
            "\uD83D\uDC3A", "\uD83D\uDC3B", "\uD83D\uDC3C", "\uD83D\uDC3D",
            "\uD83D\uDC3E", "\uD83D\uDC40", "\uD83D\uDC42", "\uD83D\uDC43",
            "\uD83D\uDC44", "\uD83D\uDC45", "\uD83D\uDC46", "\uD83D\uDC47",
            "\uD83D\uDC48", "\uD83D\uDC49", "\uD83D\uDC4A", "\uD83D\uDC4B",
            "\uD83D\uDC4C", "\uD83D\uDC4D", "\uD83D\uDC4E", "\uD83D\uDC4F",
            "\uD83D\uDC50", "\uD83D\uDC51", "\uD83D\uDC52", "\uD83D\uDC53",
            "\uD83D\uDC54", "\uD83D\uDC55", "\uD83D\uDC56", "\uD83D\uDC57",
            "\uD83D\uDC58", "\uD83D\uDC59", "\uD83D\uDC5A", "\uD83D\uDC5B",
            "\uD83D\uDC5C", "\uD83D\uDC5D", "\uD83D\uDC5E", "\uD83D\uDC5F",
            "\uD83D\uDC60", "\uD83D\uDC61", "\uD83D\uDC62", "\uD83D\uDC63",
            "\uD83D\uDC64", "\uD83D\uDC65", "\uD83D\uDC66", "\uD83D\uDC67",
            "\uD83D\uDC68", "\uD83D\uDC69", "\uD83D\uDC6A", "\uD83D\uDC6B",
            "\uD83D\uDC6C", "\uD83D\uDC6D", "\uD83D\uDC6E", "\uD83D\uDC6F",
            "\uD83D\uDC70", "\uD83D\uDC71", "\uD83D\uDC72", "\uD83D\uDC73",
            "\uD83D\uDC74", "\uD83D\uDC75", "\uD83D\uDC76", "\uD83D\uDC77",
            "\uD83D\uDC78", "\uD83D\uDC79", "\uD83D\uDC7A", "\uD83D\uDC7B",
            "\uD83D\uDC7C", "\uD83D\uDC7D", "\uD83D\uDC7E", "\uD83D\uDC7F",
            "\uD83D\uDC80", "\uD83D\uDC81", "\uD83D\uDC82", "\uD83D\uDC83",
            "\uD83D\uDC84", "\uD83D\uDC85", "\uD83D\uDC86", "\uD83D\uDC87",
            "\uD83D\uDC88", "\uD83D\uDC89", "\uD83D\uDC8A", "\uD83D\uDC8B",
            "\uD83D\uDC8C", "\uD83D\uDC8D", "\uD83D\uDC8E", "\uD83D\uDC8F",
            "\uD83D\uDC90", "\uD83D\uDC91", "\uD83D\uDC92", "\uD83D\uDC93",
            "\uD83D\uDC94", "\uD83D\uDC95", "\uD83D\uDC96", "\uD83D\uDC97",
            "\uD83D\uDC98", "\uD83D\uDC99", "\uD83D\uDC9A", "\uD83D\uDC9B",
            "\uD83D\uDC9C", "\uD83D\uDC9D", "\uD83D\uDC9E", "\uD83D\uDC9F",
            "\uD83D\uDCA0", "\uD83D\uDCA1", "\uD83D\uDCA2", "\uD83D\uDCA3",
            "\uD83D\uDCA4", "\uD83D\uDCA5", "\uD83D\uDCA6", "\uD83D\uDCA7",
            "\uD83D\uDCA8", "\uD83D\uDCA9", "\uD83D\uDCAA", "\uD83D\uDCAB",
            "\uD83D\uDCAC", "\uD83D\uDCAD", "\uD83D\uDCAE", "\uD83D\uDCAF",
            "\uD83D\uDCB0", "\uD83D\uDCB1", "\uD83D\uDCB2", "\uD83D\uDCB3",
            "\uD83D\uDCB4", "\uD83D\uDCB5", "\uD83D\uDCB6", "\uD83D\uDCB7",
            "\uD83D\uDCB8", "\uD83D\uDCB9", "\uD83D\uDCBA", "\uD83D\uDCBB",
            "\uD83D\uDCBC", "\uD83D\uDCBD", "\uD83D\uDCBE", "\uD83D\uDCBF",
            "\uD83D\uDCC0", "\uD83D\uDCC1", "\uD83D\uDCC2", "\uD83D\uDCC3",
            "\uD83D\uDCC4", "\uD83D\uDCC5", "\uD83D\uDCC6", "\uD83D\uDCC7",
            "\uD83D\uDCC8", "\uD83D\uDCC9", "\uD83D\uDCCA", "\uD83D\uDCCB",
            "\uD83D\uDCCC", "\uD83D\uDCCD", "\uD83D\uDCCE", "\uD83D\uDCCF",
            "\uD83D\uDCD0", "\uD83D\uDCD1", "\uD83D\uDCD2", "\uD83D\uDCD3",
            "\uD83D\uDCD4", "\uD83D\uDCD5", "\uD83D\uDCD6", "\uD83D\uDCD7",
            "\uD83D\uDCD8", "\uD83D\uDCD9", "\uD83D\uDCDA", "\uD83D\uDCDB",
            "\uD83D\uDCDC", "\uD83D\uDCDD", "\uD83D\uDCDE", "\uD83D\uDCDF",
            "\uD83D\uDCE0", "\uD83D\uDCE1", "\uD83D\uDCE2", "\uD83D\uDCE3",
            "\uD83D\uDCE4", "\uD83D\uDCE5", "\uD83D\uDCE6", "\uD83D\uDCE7",
            "\uD83D\uDCE8", "\uD83D\uDCE9", "\uD83D\uDCEA", "\uD83D\uDCEB",
            "\uD83D\uDCEC", "\uD83D\uDCED", "\uD83D\uDCEE", "\uD83D\uDCEF",
            "\uD83D\uDCF0", "\uD83D\uDCF1", "\uD83D\uDCF2", "\uD83D\uDCF3",
            "\uD83D\uDCF4", "\uD83D\uDCF5", "\uD83D\uDCF6", "\uD83D\uDCF7",
            "\uD83D\uDCF9", "\uD83D\uDCFA", "\uD83D\uDCFB", "\uD83D\uDCFC",
            "\uD83D\uDD00", "\uD83D\uDD01", "\uD83D\uDD02", "\uD83D\uDD03",
            "\uD83D\uDD04", "\uD83D\uDD05", "\uD83D\uDD06", "\uD83D\uDD07",
            "\uD83D\uDD08", "\uD83D\uDD09", "\uD83D\uDD0A", "\uD83D\uDD0B",
            "\uD83D\uDD0C", "\uD83D\uDD0D", "\uD83D\uDD0E", "\uD83D\uDD0F",
            "\uD83D\uDD10", "\uD83D\uDD11", "\uD83D\uDD12", "\uD83D\uDD13",
            "\uD83D\uDD14", "\uD83D\uDD15", "\uD83D\uDD16", "\uD83D\uDD17",
            "\uD83D\uDD18", "\uD83D\uDD19", "\uD83D\uDD1A", "\uD83D\uDD1B",
            "\uD83D\uDD1C", "\uD83D\uDD1D", "\uD83D\uDD1E", "\uD83D\uDD1F",
            "\uD83D\uDD20", "\uD83D\uDD21", "\uD83D\uDD22", "\uD83D\uDD23",
            "\uD83D\uDD24", "\uD83D\uDD25", "\uD83D\uDD26", "\uD83D\uDD27",
            "\uD83D\uDD28", "\uD83D\uDD29", "\uD83D\uDD2A", "\uD83D\uDD2B",
            "\uD83D\uDD2C", "\uD83D\uDD2D", "\uD83D\uDD2E", "\uD83D\uDD2F",
            "\uD83D\uDD30", "\uD83D\uDD31", "\uD83D\uDD32", "\uD83D\uDD33",
            "\uD83D\uDD34", "\uD83D\uDD35", "\uD83D\uDD36", "\uD83D\uDD37",
            "\uD83D\uDD38", "\uD83D\uDD39", "\uD83D\uDD3A", "\uD83D\uDD3B",
            "\uD83D\uDD3C", "\uD83D\uDD3D", "\uD83D\uDD50", "\uD83D\uDD51",
            "\uD83D\uDD52", "\uD83D\uDD53", "\uD83D\uDD54", "\uD83D\uDD55",
            "\uD83D\uDD56", "\uD83D\uDD57", "\uD83D\uDD58", "\uD83D\uDD59",
            "\uD83D\uDD5A", "\uD83D\uDD5B", "\uD83D\uDD5C", "\uD83D\uDD5D",
            "\uD83D\uDD5E", "\uD83D\uDD5F", "\uD83D\uDD60", "\uD83D\uDD61",
            "\uD83D\uDD62", "\uD83D\uDD63", "\uD83D\uDD64", "\uD83D\uDD65",
            "\uD83D\uDD66", "\uD83D\uDD67", "\uD83D\uDDFB", "\uD83D\uDDFC",
            "\uD83D\uDDFD", "\uD83D\uDDFE", "\uD83D\uDDFF", "\uD83D\uDE00",
            "\uD83D\uDE01", "\uD83D\uDE02", "\uD83D\uDE03", "\uD83D\uDE04",
            "\uD83D\uDE05", "\uD83D\uDE06", "\uD83D\uDE07", "\uD83D\uDE08",
            "\uD83D\uDE09", "\uD83D\uDE0A", "\uD83D\uDE0B", "\uD83D\uDE0C",
            "\uD83D\uDE0D", "\uD83D\uDE0E", "\uD83D\uDE0F", "\uD83D\uDE10",
            "\uD83D\uDE11", "\uD83D\uDE12", "\uD83D\uDE13", "\uD83D\uDE14",
            "\uD83D\uDE15", "\uD83D\uDE16", "\uD83D\uDE17", "\uD83D\uDE18",
            "\uD83D\uDE19", "\uD83D\uDE1A", "\uD83D\uDE1B", "\uD83D\uDE1C",
            "\uD83D\uDE1D", "\uD83D\uDE1E", "\uD83D\uDE1F", "\uD83D\uDE20",
            "\uD83D\uDE21", "\uD83D\uDE22", "\uD83D\uDE23", "\uD83D\uDE24",
            "\uD83D\uDE25", "\uD83D\uDE26", "\uD83D\uDE27", "\uD83D\uDE28",
            "\uD83D\uDE29", "\uD83D\uDE2A", "\uD83D\uDE2B", "\uD83D\uDE2C",
            "\uD83D\uDE2D", "\uD83D\uDE2E", "\uD83D\uDE2F", "\uD83D\uDE30",
            "\uD83D\uDE31", "\uD83D\uDE32", "\uD83D\uDE33", "\uD83D\uDE34",
            "\uD83D\uDE35", "\uD83D\uDE36", "\uD83D\uDE37", "\uD83D\uDE38",
            "\uD83D\uDE39", "\uD83D\uDE3A", "\uD83D\uDE3B", "\uD83D\uDE3C",
            "\uD83D\uDE3D", "\uD83D\uDE3E", "\uD83D\uDE3F", "\uD83D\uDE40",
            "\uD83D\uDE45", "\uD83D\uDE46", "\uD83D\uDE47", "\uD83D\uDE48",
            "\uD83D\uDE49", "\uD83D\uDE4A", "\uD83D\uDE4B", "\uD83D\uDE4C",
            "\uD83D\uDE4D", "\uD83D\uDE4E", "\uD83D\uDE4F", "\uD83D\uDE80",
            "\uD83D\uDE81", "\uD83D\uDE82", "\uD83D\uDE83", "\uD83D\uDE84",
            "\uD83D\uDE85", "\uD83D\uDE86", "\uD83D\uDE87", "\uD83D\uDE88",
            "\uD83D\uDE89", "\uD83D\uDE8A", "\uD83D\uDE8B", "\uD83D\uDE8C",
            "\uD83D\uDE8D", "\uD83D\uDE8E", "\uD83D\uDE8F", "\uD83D\uDE90",
            "\uD83D\uDE91", "\uD83D\uDE92", "\uD83D\uDE93", "\uD83D\uDE94",
            "\uD83D\uDE95", "\uD83D\uDE96", "\uD83D\uDE97", "\uD83D\uDE98",
            "\uD83D\uDE99", "\uD83D\uDE9A", "\uD83D\uDE9B", "\uD83D\uDE9C",
            "\uD83D\uDE9D", "\uD83D\uDE9E", "\uD83D\uDE9F", "\uD83D\uDEA0",
            "\uD83D\uDEA1", "\uD83D\uDEA2", "\uD83D\uDEA3", "\uD83D\uDEA4",
            "\uD83D\uDEA5", "\uD83D\uDEA6", "\uD83D\uDEA7", "\uD83D\uDEA8",
            "\uD83D\uDEA9", "\uD83D\uDEAA", "\uD83D\uDEAB", "\uD83D\uDEAC",
            "\uD83D\uDEAD", "\uD83D\uDEAE", "\uD83D\uDEAF", "\uD83D\uDEB0",
            "\uD83D\uDEB1", "\uD83D\uDEB2", "\uD83D\uDEB3", "\uD83D\uDEB4",
            "\uD83D\uDEB5", "\uD83D\uDEB6", "\uD83D\uDEB7", "\uD83D\uDEB8",
            "\uD83D\uDEB9", "\uD83D\uDEBA", "\uD83D\uDEBB", "\uD83D\uDEBC",
            "\uD83D\uDEBD", "\uD83D\uDEBE", "\uD83D\uDEBF", "\uD83D\uDEC0",
            "\uD83D\uDEC1", "\uD83D\uDEC2", "\uD83D\uDEC3", "\uD83D\uDEC4",
            "\uD83D\uDEC5",
            "1\u20E3","2\u20E3","3\u20E3","4\u20E3","5\u20E3",
            "6\u20E3","7\u20E3","8\u20E3","9\u20E3","0\u20E3","#\u20E3"};

}
