# API Tests Library from Perkbox

This library simplifies the api tests process. It is built on rest-assured and testng.



### Usage

##### POST: Sample Valid Request
```
    @Test
    public void validCreate() {
        String resourcePath = "/endpointOne";
        Responses resp = (new Requests(resourcePath))
                .withAuthorization(Token.create())
                .withBody(JsonHelper.readFile("endpointOne/CreateOne-valid"))
                .post();

        MapBuilder map = (new MapBuilder())
                .add("$.message", "The item was successfully created");

        Assert.assertTrue(resp.assertTrue(201, map));
    }
```

##### GET: Sample Valid Request
```
    @Test
    public void validGet() {
        String resourcePath = "/endpointOne/7dab6d55-755e-418e-a932-08061913142f3";
        Responses resp = (new Requests(resourcePath))
                .withAuthorization(Token.read())
                .get();

        JsonHelper json = new JsonHelper("endpointOne/CreateOne-code");

        MapBuilder map = (new MapBuilder())
                .add("$.uuid", json.getParamAsStr("$.uuid"))
                .add("$.endpoint", json.getParamAsStr("$.endpoint"))
                .add("$.create", json.getParamAsStr("$.create"));

        Assert.assertTrue(resp.assertTrue(200, map));
    }
```

##### GET-List: Sample Valid Request
```
    @Test
    public void validList() {
        String resourcePath = "/endpointTwo";
        Responses resp = (new Requests(resourcePath))
                .withAuthorization(Token.read())
                .get();

        JsonHelper json = new JsonHelper("endpointTwo/ListTwo-valid");

        MapBuilder map = (new MapBuilder())
                .add("$.data", json.getParamAsStr("$.data"));

        Assert.assertTrue(resp.assertTrue(200, map));
    }
```

##### UPDATE: Sample Valid Request
```
    @Test
    public void validUpdate () {
        String resourcePath = "/endpointTwo/7dab6d55-755e-418e-a932-08061913142f3";
        Responses resp = (new Requests(resourcePath))
                .withAuthorization(Token.update())
                .withBody(JsonHelper.readFile("endpointTwo/UpdateTwo-valid"))
                .withIfMatch("the-etag-uuid-of-this")
                .put();

        MapBuilder map = (new MapBuilder())
                .add("$.message", "The item was successfully updated");

        Assert.assertTrue(resp.assertTrue(200, map));
    }
```


