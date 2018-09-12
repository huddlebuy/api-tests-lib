package com.perkbox.util.token;

import com.perkbox.util.Config;

public class Token {

    private TokenBuilder tokenBuilder;
    private String uuid;
    private String tenant;
    private String email;

    private String readOnlyJson = "{ \"%1s\": { \"%2s\": { \"+read\": { } } } } ";
    private String createOnlyJson = "{ \"%1s\": { \"%2s\": { \"+create\": { } } } } ";
    private String updateOnlyJson = "{ \"%1s\": { \"%2s\": { \"+update\": { } } } } ";
    private String deleteOnlyJson = "{ \"%1s\": { \"%2s\": { \"+delete\": { } } } } ";
    private String createAndReadJson = "{ \"%1s\": { \"%2s\": { \"+create\": { }, \"+read\": { } } } } ";
    private String createAndUpdateJson = "{ \"%1s\": { \"%2s\": { \"+create\": { }, \"+update\": { } } } } ";
    private String updateAndReadJson = "{ \"%1s\": { \"%2s\": { \"+update\": { }, \"+read\": { } } } } ";
    private String fullJson = "{ \"%1s\": { \"%2s\": { \"+read\": { }, \"+create\": { }, \"+update\": { } } } } ";
    private String readRestrictedJson = "{ \"%1s\": { \"%2s\": { \"+read\": { \"*restricted\": \"true\" } } } } ";


    public Token(String uuid, String tenant, String email) {
        tokenBuilder = new TokenBuilder();
        this.uuid = uuid;
        this.tenant = tenant;
        this.email = email;
    }

    public String build(String jsonString, int expiry) {
        return tokenBuilder.generateToken(jsonString, uuid, tenant, email, expiry);
    }

    public String buildReadOnlyToken(String service, String endpoint) {
        return tokenBuilder.generateToken(String.format(readOnlyJson, service, endpoint), uuid, tenant, email, 3600);
    }

    public String buildCreateOnlyToken(String service, String endpoint) {
        return tokenBuilder.generateToken(String.format(createOnlyJson, service, endpoint), uuid, tenant, email, 3600);
    }

    public String buildUpdateOnlyToken(String service, String endpoint) {
        return tokenBuilder.generateToken(String.format(updateOnlyJson, service, endpoint), uuid, tenant, email, 3600);
    }

    public String buildDeleteOnlyToken(String service, String endpoint) {
        return tokenBuilder.generateToken(String.format(deleteOnlyJson, service, endpoint), uuid, tenant, email, 3600);
    }

    public String buildCreateAndReadToken(String service, String endpoint) {
        return tokenBuilder.generateToken(String.format(createAndReadJson, service, endpoint), uuid, tenant, email, 3600);
    }

    public String buildCreateAndUpdateToken(String service, String endpoint) {
        return tokenBuilder.generateToken(String.format(createAndUpdateJson,service, endpoint), uuid, tenant, email, 3600);
    }

    public String buildUpdateAndReadToken(String service, String endpoint) {
        return tokenBuilder.generateToken(String.format(updateAndReadJson, service, endpoint), uuid, tenant, email, 3600);
    }

    public String buildFullToken(String service, String endpoint) {
        return tokenBuilder.generateToken(String.format(fullJson, service, endpoint), uuid, tenant, email, 3600);
    }

    public String buildReadRestrictedToken(String service, String endpoint) {
        return tokenBuilder.generateToken(String.format(readRestrictedJson,service, endpoint), uuid, tenant, email, 3600);
    }

    public String buildExpiredReadToken(String service, String endpoint) {
        return generateAndExpire(readOnlyJson, service, endpoint);
    }

    public String buildExpiredCreateToken(String service, String endpoint) {
        return generateAndExpire(createOnlyJson, service, endpoint);
    }

    public String buildExpiredUpdateToken(String service, String endpoint) {
        return generateAndExpire(updateOnlyJson, service, endpoint);
    }

    public String buildExpiredDeleteToken(String service, String endpoint) {
        return generateAndExpire(deleteOnlyJson, service, endpoint);
    }


    // Static members

    public static String buildStatic(String jsonString, int expiry) {
        return init().build(jsonString, expiry);
    }

    public static String read() {
        return read(Config.get("ENDPOINT"));
    }

    public static String read(String endpoint) {
        return init().buildReadOnlyToken(Config.get("SERVICE"), endpoint);
    }

    public static String create() {
        return create(Config.get("ENDPOINT"));
    }

    public static String create(String endpoint) {
        return init().buildCreateOnlyToken(Config.get("SERVICE"), endpoint);
    }

    public static String update() {
        return update(Config.get("ENDPOINT"));
    }

    public static String update(String endpoint) {
        return init().buildUpdateOnlyToken(Config.get("SERVICE"), endpoint);
    }

    public static String delete() {
        return delete(Config.get("ENDPOINT"));
    }

    public static String delete(String endpoint) {
        return init().buildDeleteOnlyToken(Config.get("SERVICE"), endpoint);
    }

    public static String createAndRead() {
        return createAndRead(Config.get("ENDPOINT"));
    }

    public static String createAndRead(String endpoint) {
        return init().buildCreateAndReadToken(Config.get("SERVICE"), endpoint);
    }

    public static String createAndUpdate() {
        return createAndUpdate(Config.get("ENDPOINT"));
    }

    public static String createAndUpdate(String endpoint) {
        return init().buildCreateAndUpdateToken(Config.get("SERVICE"), endpoint);
    }

    public static String updateAndRead() {
        return updateAndRead(Config.get("ENDPOINT"));
    }

    public static String updateAndRead(String endpoint) {
        return init().buildUpdateAndReadToken(Config.get("SERVICE"), endpoint);
    }

    public static String fullPermission() {
        return fullPermission(Config.get("ENDPOINT"));
    }

    public static String fullPermission(String endpoint) {
        return init().buildFullToken(Config.get("SERVICE"), endpoint);
    }

    public static String readRestricted() {
        return readRestricted(Config.get("ENDPOINT"));
    }

    public static String readRestricted(String endpoint) {
        return init().buildReadRestrictedToken(Config.get("SERVICE"), endpoint);
    }

    public static String expiredRead() {
        return expiredRead(Config.get("ENDPOINT"));
    }

    public static String expiredRead(String endpoint) {
        return init().buildExpiredReadToken(Config.get("SERVICE"), endpoint);
    }

    public static String expiredCreate() {
        return expiredCreate(Config.get("ENDPOINT"));
    }

    public static String expiredCreate(String endpoint) {
        return init().buildExpiredCreateToken(Config.get("SERVICE"), endpoint);
    }

    public static String expiredUpdate() {
        return expiredUpdate(Config.get("ENDPOINT"));
    }

    public static String expiredUpdate(String endpoint) {
        return init().buildExpiredUpdateToken(Config.get("SERVICE"), endpoint);
    }

    public static String expiredDelete() {
        return expiredDelete(Config.get("ENDPOINT"));
    }

    public static String expiredDelete(String endpoint) {
        return init().buildExpiredDeleteToken(Config.get("SERVICE"), endpoint);
    }

    private String generateAndExpire(String permissionJsonSyntax, String service, String endpoint) {
        String token = tokenBuilder.generateToken(String.format(permissionJsonSyntax, service, endpoint), uuid, tenant, email, 1);
        try{ Thread.sleep(2000); }catch(Exception e){}
        return token;
    }

    private static Token init() {
        return new Token(Config.get("UUID"), Config.get("TENANT"), Config.get("EMAIL"));
    }

    private static boolean isNotSet(String var) {
        return (var == null || var.isEmpty());
    }
}