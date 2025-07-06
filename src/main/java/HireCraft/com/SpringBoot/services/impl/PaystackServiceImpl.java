package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.services.PaystackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PaystackServiceImpl implements PaystackService {

    @Value("${paystack.test.secret.key}")
    private String paystackSecretKey;

    @Value("${paystack.base.url:https://api.paystack.co}")
    private String paystackBaseUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(PaystackServiceImpl.class);

    // Constructor injection only - no @Autowired fields
    public PaystackServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<String, Object> initializeTransaction(String email, BigDecimal amount, String reference, String callbackUrl) {
        try {
            String url = paystackBaseUrl + "/transaction/initialize";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("email", email);
            requestBody.put("amount", amount.multiply(new BigDecimal("100")).intValue()); // Convert to kobo
            requestBody.put("reference", reference);
            requestBody.put("callback_url", callbackUrl);

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error initializing transaction: ", e);
            return createErrorResponse("Failed to initialize transaction");
        }
    }

    @Override
    public Map<String, Object> verifyTransaction(String reference) {
        try {
            String url = paystackBaseUrl + "/transaction/verify/" + reference;

            HttpHeaders headers = createHeaders();
            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error verifying transaction: ", e);
            return createErrorResponse("Failed to verify transaction");
        }
    }

    @Override
    public Map<String, Object> createSubaccount(String businessName, String settlementBank, String accountNumber, BigDecimal percentageCharge) {
        try {
            String url = paystackBaseUrl + "/subaccount";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("business_name", businessName);
            requestBody.put("settlement_bank", settlementBank);
            requestBody.put("account_number", accountNumber);
            requestBody.put("percentage_charge", percentageCharge);

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error creating subaccount: ", e);
            return createErrorResponse("Failed to create subaccount");
        }
    }

    @Override
    public Map<String, Object> updateSubaccount(String subaccountCode, Map<String, Object> updateData) {
        try {
            String url = paystackBaseUrl + "/subaccount/" + subaccountCode;

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(updateData, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error updating subaccount: ", e);
            return createErrorResponse("Failed to update subaccount");
        }
    }

    @Override
    public Map<String, Object> createSplitPayment(String name, String type, String currency, String[] subaccounts, String bearerType) {
        try {
            String url = paystackBaseUrl + "/split";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("name", name);
            requestBody.put("type", type);
            requestBody.put("currency", currency);
            requestBody.put("subaccounts", subaccounts);
            requestBody.put("bearer_type", bearerType);

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error creating split payment: ", e);
            return createErrorResponse("Failed to create split payment");
        }
    }

    @Override
    public Map<String, Object> updateSplitPayment(String splitCode, Map<String, Object> updateData) {
        try {
            String url = paystackBaseUrl + "/split/" + splitCode;

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(updateData, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error updating split payment: ", e);
            return createErrorResponse("Failed to update split payment");
        }
    }

    @Override
    public Map<String, Object> initiateTransfer(String source, String reason, BigDecimal amount, String recipient) {
        try {
            String url = paystackBaseUrl + "/transfer";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("source", source);
            requestBody.put("reason", reason);
            requestBody.put("amount", amount.multiply(new BigDecimal("100")).intValue()); // Convert to kobo
            requestBody.put("recipient", recipient);

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error initiating transfer: ", e);
            return createErrorResponse("Failed to initiate transfer");
        }
    }

    @Override
    public Map<String, Object> finalizeTransfer(String transferCode, String otp) {
        try {
            String url = paystackBaseUrl + "/transfer/finalize_transfer";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("transfer_code", transferCode);
            requestBody.put("otp", otp);

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error finalizing transfer: ", e);
            return createErrorResponse("Failed to finalize transfer");
        }
    }

    @Override
    public Map<String, Object> listBanks() {
        try {
            String url = paystackBaseUrl + "/bank";

            HttpHeaders headers = createHeaders();
            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error listing banks: ", e);
            return createErrorResponse("Failed to list banks");
        }
    }

    @Override
    public Map<String, Object> resolveAccountNumber(String accountNumber, String bankCode) {
        try {
            String url = paystackBaseUrl + "/bank/resolve?account_number=" + accountNumber + "&bank_code=" + bankCode;

            HttpHeaders headers = createHeaders();
            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error resolving account number: ", e);
            return createErrorResponse("Failed to resolve account number");
        }
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(paystackSecretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKeySpec);

            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString().equals(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error verifying webhook signature: ", e);
            return false;
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + paystackSecretKey);
        return headers;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", false);
        errorResponse.put("message", message);
        return errorResponse;
    }
}