package com.st.web;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;


public class WeatherService{
	public static final String NAMESPACE="http://WebXml.com.cn/";
    public static final String URL = "http://webservice.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl";

	private String METHOD_NAME = "getWeatherbyCityName";
	private String cityNo;

	public WeatherService(String cityNo) {
		this.cityNo = cityNo;
	}

	public String[] getWeather() {
		
		String[] arr = null;
		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			request.addProperty("theCityName", cityNo);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.bodyOut = request;
			envelope.dotNet = true;

			HttpTransportSE transport = new HttpTransportSE(URL);
			transport.debug = true;
			transport.call(NAMESPACE + METHOD_NAME, envelope);

			SoapObject result = (SoapObject) envelope.getResponse();
			int count = result.getPropertyCount();
			arr=new String[count];
			for (int i = 0; i < count; i++) {
				arr[i]=result.getProperty(i).toString();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		return arr;
	}

}
