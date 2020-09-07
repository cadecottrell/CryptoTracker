package sample;

import com.webcerebrium.binance.api.BinanceApi;
import com.webcerebrium.binance.api.BinanceApiException;

public class CryptoApi {

    private final BinanceApi api = new BinanceApi();

    protected String getPrice(String currency) throws BinanceApiException {

        String[] currencies = currency.split("/");
        if(currency.equals("BTC/USDT") || currency.equals("ETH/USDT") || currency.equals("LTC/USDT"))
        {
            return api.pricesMap().get(currencies[0] + currencies[1]).stripTrailingZeros().toPlainString();
        }
        else
        {
            return api.pricesMap().get(currencies[0] + currencies[1]).toString();
        }
    }

}