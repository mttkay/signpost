/* Copyright (c) 2009 Matthias Kaeppler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package oauth.signpost.exception;

@SuppressWarnings("serial")
public class OAuthNotAuthorizedException extends OAuthException {

    private static final String ERROR = "Authorization failed (server replied with a 401). "
        + "This can happen if the consumer key was not correct or "
        + "the signatures did not match.";
    
    private String responseBody;
    
    public OAuthNotAuthorizedException() {
        super(ERROR);
    }
    
    public OAuthNotAuthorizedException(String responseBody) {
        super(ERROR);
        this.responseBody = responseBody;
    }
    
    public String getResponseBody() {
        return responseBody;
    }
}
