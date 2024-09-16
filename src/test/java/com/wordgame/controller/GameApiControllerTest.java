package com.wordgame.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.wordgame.TestConfig;
import com.wordgame.core.JumbleEngine;
import org.springframework.test.web.servlet.MvcResult;


import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Arrays;

@WebMvcTest(GameApiController.class)
@Import(TestConfig.class)
class GameApiControllerTest {

    static final ObjectMapper OM = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Autowired
    JumbleEngine jumbleEngine;

    @Autowired
    private ObjectMapper objectMapper; // To parse the JSON

    /*
     * NOTE: Refer to "RootControllerTest.java", "GameWebControllerTest.java"
     * as reference. Search internet for resource/tutorial/help in implementing
     * the unit tests.
     *
     * Refer to "http://localhost:8080/swagger-ui/index.html" for REST API
     * documentation and perform testing.
     *
     * Refer to Postman collection ("interviewq-jumble.postman_collection.json")
     * for REST API documentation and perform testing.
     */

    @Test
    void whenCreateNewGame_thenSuccess() throws Exception {
        /*
         * Doing HTTP GET "/api/game/new"
         *
         * Input: None
         *
         * Expect: Assert these
         * a) HTTP status == 200
         * b) `result` equals "Created new game."
         * c) `id` is not null
         * d) `originalWord` is not null
         * e) `scrambleWord` is not null
         * f) `totalWords` > 0
         * g) `remainingWords` > 0 and same as `totalWords`
         * h) `guessedWords` is empty list
         */
        mvc.perform(get("/api/game/new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Created new game."))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.original_word").isNotEmpty())
                .andExpect(jsonPath("$.scramble_word").isNotEmpty())
                .andExpect(jsonPath("$.total_words").value(greaterThan(0)))
                .andExpect(jsonPath("$.remaining_words").value(greaterThan(0)))
                .andExpect(jsonPath("$.guessed_words").isEmpty());
    }

    @Test
    void givenMissingId_whenPlayGame_thenInvalidId() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Input: JSON request body
         * a) `id` is null or missing
         * b) `word` is null/anything or missing
         *
         * Expect: Assert these
         * a) HTTP status == 404
         * b) `result` equals "Invalid Game ID."
         */
        // Perform the POST request with missing 'id'
        mvc.perform(post("/api/game/guess")
                        .contentType("application/json")
                        .content("{ \"word\": \"test\" }")) // no 'id' field
                .andExpect(status().isNotFound()) // HTTP status == 404
                .andExpect(jsonPath("$.result").value("Invalid Game ID."));

        mvc.perform(post("/api/game/guess")
                        .contentType("application/json")
                        .content("{ \"id\": \"1000-1000-000uuuu\" }")) // no 'id' field
                .andExpect(status().isNotFound()) // HTTP status == 404
                .andExpect(jsonPath("$.result").value("Invalid Game ID."));

    }

    @Test
    void givenMissingRecord_whenPlayGame_thenRecordNotFound() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Input: JSON request body
         * a) `id` is some valid ID (but not exists in game system)
         * b) `word` is null/anything or missing
         *
         * Expect: Assert these
         * a) HTTP status == 404
         * b) `result` equals "Game board/state not found."
         */
        mvc.perform(post("/api/game/guess")
                        .contentType("application/json")
                        .content("{ \"id\": \"1000-1000-000uuuu\", \"word\": \"test\" }"))
                .andExpect(status().isNotFound()) // HTTP status == 404
                .andExpect(jsonPath("$.result").value("Invalid Game ID."));
    }

    @Test
    void givenCreateNewGame_whenSubmitNullWord_thenGuessedIncorrectly() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Given:
         * a) has valid game ID from previously created game
         *
         * Input: JSON request body
         * a) `id` of previously created game
         * b) `word` is null or missing
         *
         * Expect: Assert these
         * a) HTTP status == 200
         * b) `result` equals "Guessed incorrectly."
         * c) `id` equals to `id` of this game
         * d) `originalWord` is equals to `originalWord` of this game
         * e) `scrambleWord` is not null
         * f) `guessWord` is equals to `input.word`
         * g) `totalWords` is equals to `totalWords` of this game
         * h) `remainingWords` is equals to `remainingWords` of previous game state (no change)
         * i) `guessedWords` is empty list (because this is first attempt)
         */

        //has valid game ID from previously created game
        MvcResult result = mvc.perform(get("/api/game/new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Created new game."))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.original_word").isNotEmpty())
                .andExpect(jsonPath("$.scramble_word").isNotEmpty())
                .andExpect(jsonPath("$.total_words").value(greaterThan(0)))
                .andExpect(jsonPath("$.remaining_words").value(greaterThan(0)))
                .andExpect(jsonPath("$.guessed_words").isEmpty())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        String id = jsonNode.get("id").asText();
        int remainingWords = jsonNode.get("remaining_words").asInt();
        int totalWords = jsonNode.get("total_words").asInt();
        String originalWord = jsonNode.get("original_word").asText();

        mvc.perform(post("/api/game/guess")
                        .contentType("application/json")
                        .content("{ \"id\": \"" + id + "\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Guessed incorrectly."))
                .andExpect(jsonPath("$.remaining_words").value(remainingWords))
                .andExpect(jsonPath("$.total_words").value(totalWords))
                .andExpect(jsonPath("$.original_word").value(originalWord))
                .andExpect(jsonPath("$.guessed_words").isEmpty());

    }

    @Test
    void givenCreateNewGame_whenSubmitWrongWord_thenGuessedIncorrectly() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Given:
         * a) has valid game ID from previously created game
         *
         * Input: JSON request body
         * a) `id` of previously created game
         * b) `word` is some value (that is not correct answer)
         *
         * Expect: Assert these
         * a) HTTP status == 200
         * b) `result` equals "Guessed incorrectly."
         * c) `id` equals to `id` of this game
         * d) `originalWord` is equals to `originalWord` of this game
         * e) `scrambleWord` is not null
         * f) `guessWord` equals to input `guessWord`
         * g) `totalWords` is equals to `totalWords` of this game
         * h) `remainingWords` is equals to `remainingWords` of previous game state (no change)
         * i) `guessedWords` is empty list (because this is first attempt)
         */
        //has valid game ID from previously created game
        MvcResult result = mvc.perform(get("/api/game/new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Created new game."))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.original_word").isNotEmpty())
                .andExpect(jsonPath("$.scramble_word").isNotEmpty())
                .andExpect(jsonPath("$.total_words").value(greaterThan(0)))
                .andExpect(jsonPath("$.remaining_words").value(greaterThan(0)))
                .andExpect(jsonPath("$.guessed_words").isEmpty())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        String id = jsonNode.get("id").asText();
        int remainingWords = jsonNode.get("remaining_words").asInt();
        int totalWords = jsonNode.get("total_words").asInt();
        String originalWord = jsonNode.get("original_word").asText();

        mvc.perform(post("/api/game/guess")
                        .contentType("application/json")
                        .content("{ \"id\": \"" + id + "\",\"word\": \"fortnight\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.scramble_word").isNotEmpty())
                .andExpect(jsonPath("$.result").value("Guessed incorrectly."))
                .andExpect(jsonPath("$.remaining_words").value(remainingWords))
                .andExpect(jsonPath("$.total_words").value(totalWords))
                .andExpect(jsonPath("$.original_word").value(originalWord))
                .andExpect(jsonPath("$.guessed_words").isEmpty());

    }

    @Test
    void givenCreateNewGame_whenSubmitFirstCorrectWord_thenGuessedCorrectly() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Given:
         * a) has valid game ID from previously created game
         *
         * Input: JSON request body
         * a) `id` of previously created game
         * b) `word` is of correct answer
         *
         * Expect: Assert these
         * a) HTTP status == 200
         * b) `result` equals "Guessed correctly."
         * c) `id` equals to `id` of this game
         * d) `originalWord` is equals to `originalWord` of this game
         * e) `scrambleWord` is not null
         * f) `guessWord` equals to input `guessWord`
         * g) `totalWords` is equals to `totalWords` of this game
         * h) `remainingWords` is equals to `remainingWords - 1` of previous game state (decrement by 1)
         * i) `guessedWords` is not empty list
         * j) `guessWords` contains input `guessWord`
         */
        MvcResult result = mvc.perform(get("/api/game/new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Created new game."))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.original_word").isNotEmpty())
                .andExpect(jsonPath("$.scramble_word").isNotEmpty())
                .andExpect(jsonPath("$.total_words").value(greaterThan(0)))
                .andExpect(jsonPath("$.remaining_words").value(greaterThan(0)))
                .andExpect(jsonPath("$.guessed_words").isEmpty())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        String id = jsonNode.get("id").asText();
        int remainingWords = jsonNode.get("remaining_words").asInt();
        int totalWords = jsonNode.get("total_words").asInt();
        String originalWord = jsonNode.get("original_word").asText();

        mvc.perform(post("/api/game/guess")
                        .contentType("application/json")
                        .content("{ \"id\": \"" + id + "\",\"word\": \"bus\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.scramble_word").isNotEmpty())
                .andExpect(jsonPath("$.guess_word").value("bus"))
                .andExpect(jsonPath("$.result").value("Guessed correctly."))
                .andExpect(jsonPath("$.remaining_words").value(remainingWords - 1))
                .andExpect(jsonPath("$.total_words").value(totalWords))
                .andExpect(jsonPath("$.original_word").value(originalWord))
                .andExpect(jsonPath("$.guessed_words").isArray())
                .andExpect(jsonPath("$.guessed_words", hasItem("bus")));
    }

    @Test
    void givenCreateNewGame_whenSubmitAllCorrectWord_thenAllGuessed() throws Exception {
        /*
         * Doing HTTP POST "/api/game/guess"
         *
         * Given:
         * a) has valid game ID from previously created game
         * b) has submit all correct answers, except the last answer
         *
         * Input: JSON request body
         * a) `id` of previously created game
         * b) `word` is of the last correct answer
         *
         * Expect: Assert these
         * a) HTTP status == 200
         * b) `result` equals "All words guessed."
         * c) `id` equals to `id` of this game
         * d) `originalWord` is equals to `originalWord` of this game
         * e) `scrambleWord` is not null
         * f) `guessWord` equals to input `guessWord`
         * g) `totalWords` is equals to `totalWords` of this game
         * h) `remainingWords` is 0 (no more remaining, game ended)
         * i) `guessedWords` is not empty list
         * j) `guessWords` contains input `guessWord`
         */
        ArrayList<String> validSubWords = new ArrayList<>(
                Arrays.asList("baas", "cabs", "cubs", "scuba", "abs", "baa", "bus", "cab", "cub", "sac", "sub")
        );
        //scab as last word
        MvcResult result = mvc.perform(get("/api/game/new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Created new game."))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.original_word").isNotEmpty())
                .andExpect(jsonPath("$.scramble_word").isNotEmpty())
                .andExpect(jsonPath("$.total_words").value(greaterThan(0)))
                .andExpect(jsonPath("$.remaining_words").value(greaterThan(0)))
                .andExpect(jsonPath("$.guessed_words").isEmpty())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        String id = jsonNode.get("id").asText();
        int remainingWords = jsonNode.get("remaining_words").asInt();
        int totalWords = jsonNode.get("total_words").asInt();
        String originalWord = jsonNode.get("original_word").asText();

        int remainingWordsCount = 1;
        for (String word : validSubWords) {
            result = mvc.perform(post("/api/game/guess")
                            .contentType("application/json")
                            .content("{ \"id\": \"" + id + "\",\"word\": \"" + word + "\" }"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.scramble_word").isNotEmpty())
                    .andExpect(jsonPath("$.guess_word").value(word))
                    .andExpect(jsonPath("$.result").value("Guessed correctly."))
                    .andExpect(jsonPath("$.remaining_words").value(remainingWords - 1))
                    .andExpect(jsonPath("$.guessed_words").isArray())
                    .andExpect(jsonPath("$.guessed_words", hasItem(word))).andReturn();

            jsonResponse = result.getResponse().getContentAsString();
            jsonNode = objectMapper.readTree(jsonResponse);
            id = jsonNode.get("id").asText();
            remainingWords = jsonNode.get("remaining_words").asInt();
            totalWords = jsonNode.get("total_words").asInt();
            originalWord = jsonNode.get("original_word").asText();
            remainingWordsCount++;
        }
        String word = "scab";
        mvc.perform(post("/api/game/guess")
                        .contentType("application/json")
                        .content("{ \"id\": \"" + id + "\",\"word\": \"" + word + "\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.scramble_word").isNotEmpty())
                .andExpect(jsonPath("$.guess_word").value(word))
                .andExpect(jsonPath("$.result").value("All words guessed."))
                .andExpect(jsonPath("$.remaining_words").value(remainingWords - 1))
                .andExpect(jsonPath("$.original_word").value(originalWord))
                .andExpect(jsonPath("$.total_words").value(totalWords))
                .andExpect(jsonPath("$.guessed_words", hasItem(word)));
    }
}
