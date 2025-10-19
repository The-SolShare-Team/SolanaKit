import com.solana.networking.HttpNetworkDriver
import com.solana.networking.HttpRequest
import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSError
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataTaskWithRequest
import platform.Foundation.dataUsingEncoding
import platform.Foundation.setHTTPBody
import platform.Foundation.setHTTPMethod
import platform.Foundation.setValue
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class NSURLSessionException(origin: NSError): RuntimeException("Exception in http request: $origin")

class MyRequest(
    override val body: String?,
    override val method: String,
    override val properties: Map<String, String>,
    override val url: String
) : HttpRequest

class NSURLSessionNetworkDriver : HttpNetworkDriver {
    @OptIn(BetaInteropApi::class)
    override suspend fun makeHttpRequest(request: HttpRequest): String = suspendCoroutine { continuation ->
        val session = NSURLSession.sharedSession

        val nsRequest = NSMutableURLRequest(NSURL(string = request.url)).apply {
            setHTTPMethod(request.method)

            request.properties.forEach {
                setValue(it.key, forHTTPHeaderField = it.value)
            }

            request.body?.let {
                setHTTPBody(NSString.create(it).dataUsingEncoding(NSUTF8StringEncoding))
            }
        }

        print(request.url)
        print(request.method)
        print(request.body)
        print(request.properties)

        session.dataTaskWithRequest(nsRequest, {data, response, error ->
//            print(response)
            if (error != null) {
//                continuation.resumeWithException(NSURLSessionException(error))
            } else  {
                continuation.resume(if (data != null) NSString.create(data, NSUTF8StringEncoding).toString() else "")
            }
        }).resume()
    }
}
