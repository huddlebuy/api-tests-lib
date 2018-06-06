## API Tests Library from Perkbox

This library simplifies the api tests process. It is built on rest-assured and testng.



### Sample usage

#### GET: Assert status code

```
    @Test
    public void incorrectIdFormat() {
        String resourcePath = "/items/7dab6d55-755e-418e-a932-08061913142f3";
        Requests req = (new Requests(resourcePath))
                        .withAuthorization(Tokens.read())
                        .get();

        Responses resp = new Responses(req);

        Assert.assertTrue(resp.assertTrue(400));
    }
```

#### GET: Assert status code and response message

```
    @Test
    public void tokenInvalid() {
        String resourcePath = "/items/70ca6f70-aa71-11e7-abc4-cec278b6b50a";
        Requests req = (new Requests(resourcePath))
                .withAuthorization("tewtrw")
                .get();

        Responses resp = new Responses(req);

        Map<String, String> map = new HashMap<String, String>()
        {
            {
                put("$.message", "Token verification failed");
            }
        };

        Assert.assertTrue(resp.assertTrue(401, map));
    }
```

### POST: Assert status code

```
    @Test
    public void codeUrl() {
        String resourcePath = "/items";
        Requests req = (new Requests(resourcePath))
                .withAuthorization(Tokens.create())
                .withBody(JsonHelper.readFile("createItem/codeUrl"))
                .post();

        Responses resp = new Responses(req);

        Map<String, String> map = new HashMap<String, String>()
        {
            {
                put("$.message", "The item was successfully created");
            }
        };

        Assert.assertTrue(resp.assertTrue(201, map));
    }
```


