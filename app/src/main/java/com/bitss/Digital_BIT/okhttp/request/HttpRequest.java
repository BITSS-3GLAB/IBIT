package com.bitss.Digital_BIT.okhttp.request;

import java.util.LinkedList;
import java.util.List;

import okhttp3.RequestBody;

import com.bitss.Digital_BIT.Util.Constants;

public class HttpRequest {

	private Builder mBuilder;
	private String url;
	private RequestBody body;

	public HttpRequest(Builder builder) {
		this.mBuilder = builder;
	}

	public String getUrl() {
		return url;
	}

	public RequestBody getBody() {
		return body;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(String.format("url=%s", url));
		for (Parameter parameter : mBuilder.parameters) {
			sb.append(String.format(",%s=%s", parameter.getName(),
					parameter.getValue()));
		}
		sb.append("]");
		return sb.toString();
	}

	public static final class Builder {
		private HttpRequest mRequest;
		private IHttpRequestBodyBuilder mBodyBuilder;
		private List<Parameter> parameters;

		public Builder() {
			mRequest = new HttpRequest(this);
			parameters = new LinkedList<Parameter>();
		}

		public HttpRequest build() {
			if (mBodyBuilder == null) {
				StringBuilder sb = new StringBuilder(mRequest.url);
				for (int i = 0; i < parameters.size(); i++) {
					if (i == 0) {
						sb.append("?");
					} else {
						sb.append("&");
					}
					Parameter parameter = parameters.get(i);
					sb.append(parameter.getName()).append("=")
							.append(parameter.getValue());
				}
				mRequest.url = sb.toString();
			} else {
				mRequest.body = mBodyBuilder.body();
			}
			return mRequest;
		}

		public Builder url(String url) {
			String baseUrl = Constants.SERVER_URL;
			if (baseUrl.endsWith("/")) {
				baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
			}
			mRequest.url = new StringBuilder().append(baseUrl)
					.append(url).toString();
			return this;
		}

		public Builder bodyBuilder(IHttpRequestBodyBuilder builder) {
			mBodyBuilder = builder;
			mBodyBuilder.init();
			return this;
		}

		public Builder addList(List<Parameter> data) {
			if (data == null || data.size() == 0) {
				return this;
			}
			for (Parameter parameter : data) {
				add(parameter);
			}
			return this;
		}

		public Builder add(Parameter data) {
			if (mBodyBuilder == null) {
				parameters.add(data);
			} else {
				mBodyBuilder.addPart(data);
			}
			return this;
		}
	}
}
