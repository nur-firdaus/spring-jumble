package com.wordgame.controller;

import java.time.ZonedDateTime;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wordgame.core.JumbleEngine;
import com.wordgame.model.ExistsForm;
import com.wordgame.model.PrefixForm;
import com.wordgame.model.ScrambleForm;
import com.wordgame.model.SearchForm;
import com.wordgame.model.SubWordsForm;

@Controller
@RequestMapping(path = "/")
public class RootController {

    private static final Logger LOG = LoggerFactory.getLogger(RootController.class);

    private final JumbleEngine jumbleEngine;

    @Autowired(required = true)
    public RootController(JumbleEngine jumbleEngine) {
        this.jumbleEngine = jumbleEngine;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("timeNow", ZonedDateTime.now());
        return "index";
    }

    @GetMapping("scramble")
    public String doGetScramble(Model model) {
        model.addAttribute("form", new ScrambleForm());
        return "scramble";
    }

    @PostMapping("scramble")
    public String doPostScramble(
            @ModelAttribute(name = "form") ScrambleForm form,
            BindingResult bindingResult, Model model) {
        /*
         * TODO:
         * a) Validate the input `form`
         * b) To call JumbleEngine#scramble()
         * c) Presentation page to show the result
         * d) Must pass the corresponding unit tests
         */// Step a: Validate the input `form`

        String word = form.getWord();

        if (word == null || word.trim().isEmpty()) {
            bindingResult.rejectValue("word", "field.required", "must not be blank");
        } else if (word.length() < 3 || word.length() > 30) {
            bindingResult.rejectValue("word", "field.size", "size must be between 3 and 30");
        }
        if (bindingResult.hasErrors()) {
            return "scramble"; // Return the same page if there are validation errors
        }

        String scrambledWord = jumbleEngine.scramble(word);

        form.setScramble(scrambledWord);
        model.addAttribute("form", form);

        return "scramble"; // Return the view name
    }

    @GetMapping("palindrome")
    public String doGetPalindrome(Model model) {
        model.addAttribute("words", this.jumbleEngine.retrievePalindromeWords());
        return "palindrome";
    }

    @GetMapping("exists")
    public String doGetExists(Model model) {
        model.addAttribute("form", new ExistsForm());
        return "exists";
    }

    @PostMapping("exists")
    public String doPostExists(
            @ModelAttribute(name = "form") ExistsForm form,
            BindingResult bindingResult, Model model) {
        /*
         * TODO:
         * a) Validate the input `form`
         * b) To call JumbleEngine#exists()
         * c) Presentation page to show the result
         * d) Must pass the corresponding unit tests
         */
        String word = form.getWord();

        if (word == null || word.trim().isEmpty()) {
            bindingResult.rejectValue("word", "field.required", "must not be blank");
        } else if (word.length() < 3 || word.length() > 30) {
            bindingResult.rejectValue("word", "field.size", "size must be between 3 and 30");
        }
        if (bindingResult.hasErrors()) {
            return "exists"; // Return the same page if there are validation errors
        }

        word=word!=null?word.trim():word;

        boolean isExist = jumbleEngine.exists(word);

        LOG.info("exists {}",isExist);
        form.setExists(isExist);
        model.addAttribute("form", form);

        return "exists";
    }

    @GetMapping("prefix")
    public String doGetPrefix(Model model) {
        model.addAttribute("form", new PrefixForm());
        return "prefix";
    }

    @PostMapping("prefix")
    public String doPostPrefix(
            @ModelAttribute(name = "form") PrefixForm form,
            BindingResult bindingResult, Model model) {
        /*
         * TODO:
         * a) Validate the input `form`
         * b) To call JumbleEngine#wordsMatchingPrefix()
         * c) Presentation page to show the result
         * d) Must pass the corresponding unit tests
         */

        String word = form.getPrefix();

        if (word == null || word.trim().isEmpty()) {
            bindingResult.rejectValue("prefix", "field.required", "must not be blank");
        }
        if (bindingResult.hasErrors()) {
            return "prefix"; // Return the same page if there are validation errors
        }

        word=word!=null?word.trim():word;

        Collection<String> words = jumbleEngine.wordsMatchingPrefix(word);

        LOG.info("prefix {}",words);
        form.setWords(words);
        model.addAttribute("form", form);
        return "prefix";
    }

    @GetMapping("search")
    public String doGetSearch(Model model) {
        model.addAttribute("form", new SearchForm());
        return "search";
    }

    @PostMapping("search")
    public String doPostSearch(
            @ModelAttribute(name = "form") SearchForm form,
            BindingResult bindingResult, Model model) {
        /*
         * TODO:
         * a) Validate the input `form`
         * b) Show the fields error accordingly: "Invalid startChar", "Invalid endChar", "Invalid length".
         * c) To call JumbleEngine#searchWords()
         * d) Presentation page to show the result
         * e) Must pass the corresponding unit tests
         */

        String startChar = form.getStartChar();
        String endChar = form.getEndChar();
        Integer lengthChar = 0;

        try {
            lengthChar= Integer.valueOf(form.getLength());
        }catch (Exception e){
            bindingResult.rejectValue("length", "field.required", "Failed to convert property value of type java.lang.String to required type java.lang.Integer for property length; nested exception is java.lang.NumberFormatException: For input string:");
        }

        Character startC=null;
        Character endC=null;
        if (startChar == null || startChar.trim().isEmpty()) {
            bindingResult.rejectValue("startChar", "field.required", "Invalid startChar");
        }else{
            startC=startChar.charAt(0);
        }
        if (endChar == null || endChar.trim().isEmpty()) {
            bindingResult.rejectValue("endChar", "field.required", "Invalid endChar");
        }else{
            endC=endChar.charAt(0);
        }
        if (lengthChar<=0) {
            bindingResult.rejectValue("length", "field.required", "Invalid length");
        }

        if((startChar !=null && startChar.length()>1)||(endChar !=null && endChar.length()>1)){
            bindingResult.rejectValue("startChar", "field.required", "size must be between 0 and 1");
            bindingResult.rejectValue("endChar", "field.required", "size must be between 0 and 1");
            return "search";
        }


        Collection<String> words = jumbleEngine.searchWords(startC, endC, lengthChar);

        LOG.info("search {}",words);
        form.setWords(words);
        model.addAttribute("form", form);

        return "search";
    }

    @GetMapping("subWords")
    public String goGetSubWords(Model model) {
        model.addAttribute("form", new SubWordsForm());
        return "subWords";
    }

    @PostMapping("subWords")
    public String doPostSubWords(
            @ModelAttribute(name = "form") SubWordsForm form,
            BindingResult bindingResult, Model model) {
        /*
         * TODO:
         * a) Validate the input `form`
         * b) To call JumbleEngine#generateSubWords()
         * c) Presentation page to show the result
         * d) Must pass the corresponding unit tests
         */

        if (form.getWord() == null || form.getWord().trim().isEmpty()) {
            bindingResult.rejectValue("word", "field.required", "Invalid Word");
            return "subWords";
        }

        form.setWords(jumbleEngine.generateSubWords(form.getWord().trim(),form.getMinLength()));

        model.addAttribute("form", form);
        return "subWords";
    }

}
