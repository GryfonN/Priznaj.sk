package sk.gryfonnlair.priznaj.control.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import sk.gryfonnlair.priznaj.PriznajApplication;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


/**
 * Singleton trieda obsahujuca HumanReadable metody site na mieru, ak
 * nieco potrebujem z servera zavolam si tuto metodu a ta sa postara uz o
 * konvert udajov/ spravnu formu udajov atd atd<br>
 * <p>
 * 
 * @author gryfonn
 * 
 */
public class RestDroid {

	//Connection data
	private static final String SERVER_IP = "http://www.priznaj.sk";
	private static final String REST_COMMUNICATION_VERSION = "/rest/1_0";
	private static final String REST_ADD_ADMISSION = "/add_admission.php";
	private static final String REST_GET_ADMISSIONS = "/get_admissions.php";
	private static final String REST_GET_NEWS = "/get_news.php";
	private static final int TIMEOUT_CONNECTION = 5000;
	/**
	 * pocet priznani kt by mali prist z getOlder , je to setnute v php skripte
	 * ja na tejto cifre staviam podmienky
	 */
	public static final int GET_OLDER_COUNT = 200;
	//standart headers
	//getNews:
	public static final String HEADER_PRIZNANIA = "priznania";
	public static final String HEADER_PRIZNANIA2 = "priznania2";
	public static final String HEADER_PRIZNANIA_STREDNE = "priznania-stredne";
	//add_admission
	public static final String HEADER_TYPE = "type";
	public static final String HEADER_TEXT = "text";
	public static final String HEADER_DATETIME = "datetime";
	public static final String HEADER_GENDER = "gender";
	public static final String HEADER_REGION = "region";
	public static final String HEADER_COUNTY = "county";
	public static final String HEADER_HIGH_SCHOOL = "highschool";
	public static final String HEADER_UNIVERSITY = "university";
	//get_admissions
	public static final String HEADER_INIT = "init";
	public static final String HEADER_TABLE = "table";
	public static final String HEADER_NEWEST_ID = "newest-id";
	public static final String HEADER_OLDEST_ID = "oldest-id";


	public volatile static boolean networkAvailable = false;

	/**
	 * Podla contextu zisti ci je prijenie na net, spusta sa iba raz v pred
	 * spustenim vlakna vo splashi, a nielen ze vrati boolean ale aj setne
	 * public static premmenu pod sebou takze je sa mozne na nu odkazovat z
	 * celej apky
	 * 
	 * @param context
	 * @return boolean ci ide net
	 */
	public static void isNetworkConnected(final Context context) {
		if (context != null) {
			final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo info = cm.getActiveNetworkInfo();
			if (info != null && info.getState() == NetworkInfo.State.CONNECTED) {
				networkAvailable = true;
				if (PriznajApplication.D) {
					Log.d(PriznajApplication.DEBUG_TAG, "RestDroid>isNetworkConnected = " + networkAvailable);
				}
				return;
			}
		}
		networkAvailable = false;
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "RestDroid>isNetworkConnected = " + networkAvailable);
		}
	}

	/**
	 * posle admission na server, vysklada z hlaviciek request, hlavicka v tvare
	 * "header":"value"
	 * 
	 * @param headers
	 * @return 1 ak uspesne a response code 200
	 */
	public static byte sendAdmission(final String... headers) {
		final DefaultHttpClient httpclient = new DefaultHttpClient();
		final HttpPost httpPost = new HttpPost(SERVER_IP + REST_COMMUNICATION_VERSION + REST_ADD_ADMISSION);
		String[] temp;
		for (final String headerDuo : headers) {
			//koli encodingu som spravil vynimku a text davam ako raw data nie ako header
			if (headerDuo.startsWith("text")) {
				try {
					final StringEntity entity = new StringEntity(headerDuo.substring(5), HTTP.UTF_8);
					httpPost.setEntity(entity);
				} catch (final Exception e) {
					if (PriznajApplication.D) {
						Log.e(PriznajApplication.DEBUG_TAG, "RestDroid>sendAdmission> text encoding" + e.getMessage());
					}
					return 0;
				}
			} else {
				temp = headerDuo.split(":");
				httpPost.setHeader(temp[0], temp[1]);
			}
		}
		httpPost.setHeader("Content-Type", "text/html; charset=utf-8");
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpclient.execute(httpPost);
		} catch (final ClientProtocolException e) {
			if (PriznajApplication.D) {
				Log.e(PriznajApplication.DEBUG_TAG, "RestDroid>sendAdmission>" + e.getMessage());
			}
			return 0;
		} catch (final IOException e) {
			if (PriznajApplication.D) {
				Log.e(PriznajApplication.DEBUG_TAG, "RestDroid>sendAdmission>" + e.getMessage());
			}
			return 0;
		}
		if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * GET request s hlavickou 'init':'0' pre ziskanie init priznani
	 * 
	 * @return JSON ako String
	 */
	public static String getInitAdmissions() {
		final HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				TIMEOUT_CONNECTION);
		HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_CONNECTION);
		final DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);

		final HttpGet httpGet = new HttpGet(SERVER_IP + REST_COMMUNICATION_VERSION + REST_GET_ADMISSIONS);

		httpGet.setHeader(HEADER_INIT, "0");
		HttpResponse httpResponse = null;
		InputStream responseStream = null;
		try {
			httpResponse = httpclient.execute(httpGet);
			if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200 && httpResponse.getEntity() != null)
			{
				responseStream = httpResponse.getEntity().getContent();
			}
		} catch (final ClientProtocolException e) {
			if (PriznajApplication.D) {
				Log.e(PriznajApplication.DEBUG_TAG, "RestDroid>getInitAdmissions>" + e.getMessage());
			}
			return null;
		} catch (final IOException e) {
			if (PriznajApplication.D) {
				Log.e(PriznajApplication.DEBUG_TAG, "RestDroid>getInitAdmissions>" + e.getMessage());
			}
			return null;
		}
		if (responseStream != null) {
			return readInputStream(responseStream);
		}
		else {
			return null;
		}
	}

	/**
	 * GET request s hlavickami pre vyber vsetkych novsich priznani UNI ako mam
	 * 
	 * @param newestId id mojho najnovsieho priznania (najvyssie id)
	 * @return JSON ako String
	 */
	public static String getNewUniAdmissions(final String newestId) {
		return newestId != null ? getNewestAdmissions("priznania", newestId) : getOldestAdmissions("priznania", null);
	}

	/**
	 * GET request s hlavickami pre vyber vsetkych novsich priznani GB ako mam
	 * 
	 * @param newestId id mojho najnovsieho priznania (najvyssie id)
	 * @return JSON ako String
	 */
	public static String getNewGBAdmissions(final String newestId) {
		return getNewestAdmissions("priznania2", newestId);
	}

	/**
	 * GET request s hlavickami pre vyber vsetkych novsich priznani HS ako mam
	 * 
	 * @param newestId id mojho najnovsieho priznania (najvyssie id)
	 * @return JSON ako String
	 */
	public static String getNewHSAdmissions(final String newestId) {
		return getNewestAdmissions("priznania_stredne", newestId);
	}

	/**
	 * GET request s hlavickami 'table_name':'hightestID' , vrati pocet priznani
	 * v prislusnych kategoriach
	 * 
	 * @return JSON ako string
	 */
	public static String getNews(final Map<String, String> map) {
		final HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				TIMEOUT_CONNECTION);
		HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_CONNECTION);
		final DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);

		final HttpGet httpGet = new HttpGet(SERVER_IP + REST_COMMUNICATION_VERSION + REST_GET_NEWS);

		httpGet.setHeader(HEADER_PRIZNANIA, map.get("priznania"));
		httpGet.setHeader(HEADER_PRIZNANIA2, map.get("priznania2"));
		httpGet.setHeader(HEADER_PRIZNANIA_STREDNE, map.get("priznania_stredne"));
		HttpResponse httpResponse = null;
		InputStream responseStream = null;

		try {
			httpResponse = httpclient.execute(httpGet);
			if (PriznajApplication.D && httpResponse != null) {
				Log.d(PriznajApplication.DEBUG_TAG, "RestDroid> getNews: response=" + httpResponse.getStatusLine().getStatusCode());
			}
			if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200 && httpResponse.getEntity() != null)
			{
				responseStream = httpResponse.getEntity().getContent();
			}
		} catch (final ClientProtocolException e) {
			if (PriznajApplication.D) {
				Log.e(PriznajApplication.DEBUG_TAG, "RestDroid>getNews>" + e.getMessage());
			}
			return "";
		} catch (final IOException e) {
			if (PriznajApplication.D) {
				Log.e(PriznajApplication.DEBUG_TAG, "RestDroid>getNews>" + e.getMessage());
			}
			return "";
		}
		if (responseStream != null) {
			return readInputStream(responseStream);
		}
		else {
			return "";
		}
	}

	/**
	 * GET request s hlavickami pre vyber dalsich starsich priznani UNI ako mam
	 * 
	 * @param oldestId id mojho najstarsieho priznania (najnyzsie id)
	 * @return JSON ako String
	 */
	public static String getOlderUniAdmissions(final String oldestId) {
		return getOldestAdmissions("priznania", oldestId);
	}

	/**
	 * GET request s hlavickami pre vyber dalsich starsich priznani GB ako mam
	 * 
	 * @param oldestId id mojho najstarsieho priznania (najnyzsie id)
	 * @return JSON ako String
	 */
	public static String getOlderGBAdmissions(final String oldestId) {
		return getOldestAdmissions("priznania2", oldestId);
	}

	/**
	 * GET request s hlavickami pre vyber dalsich starsich priznani HS ako mam
	 * 
	 * @param oldestId id mojho najstarsieho priznania (najnyzsie id)
	 * @return JSON ako String
	 */
	public static String getOlderHSAdmissions(final String oldestId) {
		return getOldestAdmissions("priznania_stredne", oldestId);
	}

	/**
	 * Z input stremu spravi string, prevanze sa takto dobracujem v restdroide k
	 * JSONovi kt mi pride ako content
	 * 
	 * @param instream response content
	 * @return string JSON
	 */
	private static String readInputStream(final InputStream instream) {
		final StringBuilder sb = new StringBuilder();
		try {
			final BufferedReader r = new BufferedReader(new InputStreamReader(
					instream));
			for (String line = r.readLine(); line != null; line = r.readLine()) {
				sb.append(line);
			}

			instream.close();

		} catch (final IOException e) {
		} finally {
			try {
				instream.close();
			} catch (final IOException e) {
			}
		}
		return sb.toString();
	}

	/**
	 * telo pre getNewAdmissions metody
	 * 
	 * @param table
	 * @param id
	 * @return
	 */
	private static String getNewestAdmissions(final String table, final String id) {
		final HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_CONNECTION);
		HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_CONNECTION);
		final DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);

		final HttpGet httpGet = new HttpGet(SERVER_IP + REST_COMMUNICATION_VERSION + REST_GET_ADMISSIONS);

		httpGet.setHeader(HEADER_TABLE, table);
		httpGet.setHeader(HEADER_NEWEST_ID, id);
		HttpResponse httpResponse = null;
		InputStream responseStream = null;

		try {
			httpResponse = httpclient.execute(httpGet);
			if (PriznajApplication.D && httpResponse != null) {
				Log.d(PriznajApplication.DEBUG_TAG, "RestDroid> getNewestAdmissions " + table + ",newId=" + id + ", reponse="
						+ httpResponse.getStatusLine().getStatusCode());
			}
			if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200 && httpResponse.getEntity() != null)
			{
				responseStream = httpResponse.getEntity().getContent();
			}
		} catch (final ClientProtocolException e) {
			if (PriznajApplication.D) {
				Log.e(PriznajApplication.DEBUG_TAG, "RestDroid>getNewestAdmissions>" + e.getMessage());
			}
			return null;
		} catch (final IOException e) {
			if (PriznajApplication.D) {
				Log.e(PriznajApplication.DEBUG_TAG, "RestDroid>getNewestAdmissions>" + e.getMessage());
			}
			return null;
		}
		if (responseStream != null) {
			return readInputStream(responseStream);
		}
		else {
			return null;
		}
	}

	/**
	 * telo pre getOlderAdmissions metody
	 * 
	 * @param table
	 * @param oldestId
	 * @return
	 */
	private static String getOldestAdmissions(final String table, final String oldestId) {
		final HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_CONNECTION);
		HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_CONNECTION);
		final DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);

		final HttpGet httpGet = new HttpGet(SERVER_IP + REST_COMMUNICATION_VERSION + REST_GET_ADMISSIONS);

		httpGet.setHeader(HEADER_TABLE, table);
		//hack ak prazdnu tabulku tak proste stiahne top 100 apriznani
		httpGet.setHeader(HEADER_OLDEST_ID, oldestId != null ? oldestId : "999999");
		HttpResponse httpResponse = null;
		InputStream responseStream = null;

		try {
			httpResponse = httpclient.execute(httpGet);
			if (PriznajApplication.D && httpResponse != null) {
				Log.d(PriznajApplication.DEBUG_TAG, "RestDroid> getOldestAdmissions " + table + ",newId=" + oldestId + ", reponse="
						+ httpResponse.getStatusLine().getStatusCode());
			}
			if (httpResponse.getStatusLine().getStatusCode() == 200 && httpResponse.getEntity() != null)
			{
				responseStream = httpResponse.getEntity().getContent();
			} else if (httpResponse.getStatusLine().getStatusCode() == 204) {
				return new String();
			}
		} catch (final ClientProtocolException e) {
			if (PriznajApplication.D) {
				Log.e(PriznajApplication.DEBUG_TAG, "RestDroid>getOldestAdmissions>" + e.getMessage());
			}
			return null;
		} catch (final IOException e) {
			if (PriznajApplication.D) {
				Log.e(PriznajApplication.DEBUG_TAG, "RestDroid>getOldestAdmissions>" + e.getMessage());
			}
			return null;
		}
		if (responseStream != null) {
			return readInputStream(responseStream);
		}
		else {
			return null;
		}
	}
}
