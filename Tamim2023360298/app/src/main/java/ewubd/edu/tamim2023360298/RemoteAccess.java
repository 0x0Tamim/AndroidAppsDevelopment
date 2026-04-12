package ewubd.edu.tamim2023360298;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.utils.URLEncodedUtils;
import java.io.*;
import java.util.List;
import java.net.*;

@SuppressWarnings("ALL")
public class RemoteAccess {
    private RemoteAccess(){}
    private static RemoteAccess instance = new RemoteAccess();

    public static RemoteAccess getInstance(){
        return instance;
    }

    public String makeHttpRequest(String url, String method, List<NameValuePair> params) {
        HttpURLConnection http = null;
        InputStream is = null;
        String data = "";
        try {
            if (method.equals("POST")) {
                if(params != null) {
                    String paramString = URLEncodedUtils.format(params, "utf-8");
                    url += "?" + paramString;
                }
            }
            System.out.println("@RemoteAccess-" + ": " + url);
            URL urlc = new URL(url);
            http = (HttpURLConnection) urlc.openConnection();
            http.connect();
            is = http.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            http.disconnect();
        } catch (Exception e) {
        }
        return null;
    }
}