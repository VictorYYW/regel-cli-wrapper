package edu.stanford.nlp.sempre.interactive;

import edu.stanford.nlp.sempre.LanguageAnalyzer;
import edu.stanford.nlp.sempre.LanguageInfo;

import java.util.Arrays;

/**
 * LanguageAnalyzer for DAL, basically a lexer
 *
 * @author sidaw
 */
public class DALAnalyzer extends LanguageAnalyzer {
  // Stanford tokenizer doesn't break hyphens.
  // Replace hypens with spaces for utterances like
  // "Spanish-speaking countries" but not for "2012-03-28".
  public static String breakHyphens(String utterance) {
    StringBuilder buf = new StringBuilder(utterance);
    for (int i = 0; i < buf.length(); i++) {
      if (buf.charAt(i) == '-' && (i + 1 < buf.length() && Character.isLetter(buf.charAt(i + 1))))
        buf.setCharAt(i, ' ');
    }
    return buf.toString();
  }

  private static final String[] numbers = { "zero", "one", "two", "three", "four", "five", "six", "seven", "eight",
      "nine", "ten" };

  @Override
  public LanguageInfo analyze(String utterance) {
    LanguageInfo languageInfo = new LanguageInfo();

    // Clear these so that analyze can hypothetically be called
    // multiple times.
    languageInfo.tokens.clear();
    languageInfo.posTags.clear();
    languageInfo.nerTags.clear();
    languageInfo.nerValues.clear();
    languageInfo.lemmaTokens.clear();

    // Break hyphens
    utterance = breakHyphens(utterance);

    // Default analysis - create tokens crudely
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < utterance.length(); i++) {
      char c = utterance.charAt(i);
      // Put whitespace around certain characters.
      boolean boundaryBefore = !(i - 1 >= 0) || utterance.charAt(i - 1) == ' ';
      boolean boundaryAfter = !(i + 1 < utterance.length()) || utterance.charAt(i + 1) == ' ';
      boolean separate = false;
      if (c == '.') // Break off period if already space around it (to preserve
                    // numbers like 3.5)
        separate = boundaryBefore || boundaryAfter;
      else if (c == '=') // separate all >, =, < except >=, <=
        separate = !(i - 1 >= 0) || ((utterance.charAt(i - 1) != '>' && utterance.charAt(i - 1) != '<'));
      else if (c == '>' || c == '<')
        separate = !(i + 1 < utterance.length())
            || ((utterance.charAt(i + 1) != '=' && utterance.charAt(i + 1) != '='));
      else
        separate = (",?'\"[];{}+-".indexOf(c) != -1);

      if (separate)
        buf.append(' ');
      // Convert quotes
      if (c == '"')
        buf.append(boundaryBefore ? "``" : "''");
      else if (c == '\'')
        buf.append(boundaryBefore ? "`" : "'");
      else if (c == '>' || c == '<') {
        buf.append(' ');
        buf.append(c);
      } else if (c == '=') {
        buf.append(c);
        buf.append(' ');
      } else
        buf.append(c);
      if (separate)
        buf.append(' ');
    }
    utterance = buf.toString().trim();
    if (!utterance.equals("")) {
      String[] tokens = utterance.split("\\s+");
      for (String token : tokens) {
        String lemma = token;
        if (token.endsWith("s") && token.length() > 1)
          lemma = token.substring(0, token.length() - 1);

        languageInfo.tokens.add(LanguageAnalyzer.opts.lowerCaseTokens ? token.toLowerCase() : token);
        languageInfo.lemmaTokens.add(LanguageAnalyzer.opts.lowerCaseTokens ? lemma.toLowerCase() : lemma);

        // Is it a written out number?
        int x = Arrays.asList(numbers).indexOf(token);
        if (x != -1) {
          languageInfo.posTags.add("CD");
          languageInfo.nerTags.add("NUMBER");
          languageInfo.nerValues.add(x + "");
          continue;
        }

        try {
          Double.parseDouble(token);
          languageInfo.posTags.add("CD");
          languageInfo.nerTags.add("NUMBER");
          languageInfo.nerValues.add(token);
        } catch (NumberFormatException e) {
          // Guess that capitalized nouns are proper
          if (Character.isUpperCase(token.charAt(0)))
            languageInfo.posTags.add("NNP");
          else if (token.equals("'") || token.equals("\"") || token.equals("''") || token.equals("``"))
            languageInfo.posTags.add("''");
          else
            languageInfo.posTags.add("UNK");
          languageInfo.nerTags.add("UNK");
          languageInfo.nerValues.add("UNK");
        }
      }
    }
    return languageInfo;
  }
}
