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

        resp.log("validCreate"); // log response and statusCode

        Assert.assertTrue(resp.assertMatch(201, "\\{\"links\":\\{\"self\":\"/v1/endpointOne/" + Regex.UUID + "\"\\}\\}"));
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

        Assert.assertTrue(resp.assertTrue(200, map, true)); // Third true parameter is to log expected and actual values
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

        String updatedBody = JsonHelper.modifyParams("endpointTwo/UpdateTwo-valid", (new MapBuilder())
                .add("$.endpoint", "twoA")
                .add("$.update", "twoA"));

        Responses resp = (new Requests(resourcePath))
                .withAuthorization(Token.update())
                .withBody(updatedBody)
                .withIfMatch("4e666f2a4fa329c100016d239fad257f")
                .put();

        MapBuilder result = (new MapBuilder())
                .add("$.message", "The item was successfully updated");

        Assert.assertTrue(resp.assertTrue(200, result));
    }
```


