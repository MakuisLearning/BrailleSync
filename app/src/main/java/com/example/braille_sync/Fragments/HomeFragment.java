package com.example.braille_sync.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.braille_sync.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HomeFragment extends Fragment {

    FirebaseFirestore db;
    String uid;

    private static final Map<Character, Character> digitToAscii = new HashMap<>();
    private static final Map<Character,String> CharToBraille = new HashMap<>();
    private static final Map<Character,String> symbolToAscii = new HashMap<>();


    static {
        digitToAscii.put('1', 'a');
        digitToAscii.put('2', 'b');
        digitToAscii.put('3', 'c');
        digitToAscii.put('4', 'd');
        digitToAscii.put('5', 'e');
        digitToAscii.put('6', 'f');
        digitToAscii.put('7', 'g');
        digitToAscii.put('8', 'h');
        digitToAscii.put('9', 'i');
        digitToAscii.put('0', 'j');
        symbolToAscii.put('&',"`&");//
        symbolToAscii.put('=',"\"7");//
        symbolToAscii.put('<',"`<");//
        symbolToAscii.put('>',"`>");//
        symbolToAscii.put('\'',"'");
        symbolToAscii.put('%',".0");//
        symbolToAscii.put('*',"\"9");//
        symbolToAscii.put('@',"`a");//
        symbolToAscii.put('/',"_/");//
        symbolToAscii.put('\\',"_\\");//
        symbolToAscii.put('{',"_<");//
        symbolToAscii.put('}',"_>");//
        symbolToAscii.put('(',"\"<");//
        symbolToAscii.put(')',"\">");//
        symbolToAscii.put('[',".<");//
        symbolToAscii.put(']',".>");//
        symbolToAscii.put(':',"3");//
        symbolToAscii.put(',',"1");//
        symbolToAscii.put('!',"6");//
        symbolToAscii.put('-',"-"); //
        symbolToAscii.put('+',"\"6");//
        symbolToAscii.put('?',"8");//
        symbolToAscii.put('$',"`s");//
        symbolToAscii.put('^',"`5");//
        symbolToAscii.put('_',".-");//
        symbolToAscii.put('\"',",7");//
        symbolToAscii.put('.',"4");//
        symbolToAscii.put(';',"2");//
        symbolToAscii.put('#',"_?");//
        symbolToAscii.put('°',"~j");//
        // symbolToAscii.put('“',"~8");// ! Unable to read
        // symbolToAscii.put('”',"~0");//! Unable to read
        symbolToAscii.put('÷',"\"/");//
        // symbolToAscii.put('ñ',"~]n");// ! Unable to read
        symbolToAscii.put('|',"_|");//
        // symbolToAscii.put('ü',"~3u");// ! Unable to read
        // symbolToAscii.put('×',"\"8");// ! Unable to read

        CharToBraille.put('a', "100000"); // * @ :: `a
        CharToBraille.put('b', "101000");
        CharToBraille.put('c', "110000");
        CharToBraille.put('d', "110100");
        CharToBraille.put('e', "100100");
        CharToBraille.put('f', "111000");
        CharToBraille.put('g', "111100");
        CharToBraille.put('h', "101100");
        CharToBraille.put('i', "011000");
        CharToBraille.put('j', "011100"); // * ° :: ~j
        CharToBraille.put('k', "100010");
        CharToBraille.put('l', "101010");
        CharToBraille.put('m', "110010");
        CharToBraille.put('n', "110110"); // * ñ :: ~]n
        CharToBraille.put('o', "100110");
        CharToBraille.put('p', "111010");
        CharToBraille.put('q', "111110");
        CharToBraille.put('r', "101110");
        CharToBraille.put('s', "011010"); //* $ :: `s
        CharToBraille.put('t', "011110");
        CharToBraille.put('u', "100011");
        CharToBraille.put('v', "101011");
        CharToBraille.put('w', "011101");
        CharToBraille.put('x', "110011");
        CharToBraille.put('y', "110111");
        CharToBraille.put('z', "100111");
        CharToBraille.put('0',"000111"); //* % :: .0 // ” :: ~0
        CharToBraille.put('1',"001000"); //* 1 :: ,
        CharToBraille.put('2',"001010"); //* 2 :: ;
        CharToBraille.put('3',"010010"); //* 3 :: :
        CharToBraille.put('4',"001101"); //* 4 :: .
        CharToBraille.put('5',"001001"); //* ^ :: `5
        CharToBraille.put('6',"001110"); //* 6 :: * // + :: "6
        CharToBraille.put('7',"001111"); //* = :: "7 // " :: ,7
        CharToBraille.put('8',"001011"); //* / ? :: 8 // “ :: ~8
        CharToBraille.put('9',"000110"); //* / * :: "9
        CharToBraille.put('`',"010000");
        CharToBraille.put('&',"111011"); //* & :: `&
        CharToBraille.put('"',"000100");
        CharToBraille.put('<',"101001"); //* < :: `< // { :: _< // ( :: "< // [ :: .<
        CharToBraille.put('>',"010110"); //* > :: `> // } :: _> // ) :: "> // ] :: .>
        CharToBraille.put('\'',"000010"); //* '
        CharToBraille.put('_',"010101");
        CharToBraille.put('/',"010010"); //* / :: _/ // ÷ :: "/
        CharToBraille.put('\\',"010010"); //* \ :: _\
        CharToBraille.put('?',"110101"); //* # :: _?
        CharToBraille.put('-',"000011"); //* _ :: .-
        CharToBraille.put('.',"010001");
        CharToBraille.put(',',"000001");
        CharToBraille.put('|',"101101"); //* | :: _|
        CharToBraille.put(']',"101101");
        CharToBraille.put('~',"010100");
        CharToBraille.put('#', "010111");
        CharToBraille.put(' ', "000000");
    }

    public HomeFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Button button = view.findViewById(R.id.submitBtn);
        EditText textInput = view.findViewById(R.id.textInput);
        TextView textLengthView = view.findViewById(R.id.textView2);
        TextView Error = view.findViewById(R.id.errorText);

        textInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains("\n")) {
                    textInput.setText(s.toString().replace("\n", ""));
                    textInput.setSelection(textInput.getText().length());
                }
                int length = s.length();
                textLengthView.setText(getString(R.string.text_length, length));
                if(length > 200){
                    textInput.setTextColor(Color.RED);
                    textLengthView.setTextColor(Color.RED);
                }else{
                    textInput.setTextColor(Color.WHITE);
                    textLengthView.setTextColor(Color.WHITE);
                }
          }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        db = FirebaseFirestore.getInstance();

        getParentFragmentManager().setFragmentResultListener("Data", this, (requestKey, result) -> {
                    String text = result.getString("text");
                    textInput.setText(text);
                });


        button.setOnClickListener(v -> {

            String input = textInput.getText().toString();


            if (input.isEmpty() || input.trim().isEmpty()){
                Error.setText("Please enter some text");
                return;
            }
            if (input.length() > 200){
                Error.setText("Text maximum limit is reached");
                return;
            }

            ArrayList<String> result = wrapAscii(textToAscii(input),30,27);
            ArrayList<String> BrailleCode = brailleletter(result);
            //print the Braille somewhere

            //pass value to firebase firestore
            textInput.getText().clear();
            uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            Map<String, Object> history = new HashMap<>();
            history.put("text", input);
            history.put("uid", uid);
            history.put("time", Timestamp.now());


            db.collection("user-history")
                    .add(history)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentReference documentReference = task.getResult();
                            Log.d("TAGOFWAR", "DocumentSnapshot successfully written with ID: " + documentReference.getId());
                        } else {
                            Log.w("TAGOFWAR", "Error adding document", task.getException());
                        }
                    })
                    .addOnFailureListener(e -> Log.w("TAGOFWAR", "Error adding document", e));
        });


        return view;
    }

    public static String textToAscii(String input) {
        StringBuilder result = new StringBuilder();
        int length = input.length();
        boolean isOnUppercaseSequence = false;
        boolean isOnNumberSequence = false;
        StringBuilder numberSegment = new StringBuilder();

        for (int i = 0; i < length; i++) {
            char currentChar = input.charAt(i);

            if (Character.isUpperCase(currentChar)) {
                int j = i + 1;
                while (j < length && Character.isUpperCase(input.charAt(j))) {
                    j++;
                }

                if (isOnNumberSequence) {
                    result.append(numberSegment).append(";");
                    numberSegment.setLength(0);
                    isOnNumberSequence = false;
                }

                String upperCaseSegment = input.substring(i, j);
                if (upperCaseSegment.length() > 1) {
                    result.append(",,").append(upperCaseSegment);
                } else {
                    result.append(",").append(upperCaseSegment);
                }
                isOnUppercaseSequence = upperCaseSegment.length() > 1;
                i = j - 1;

            } else if (Character.isLowerCase(currentChar)) {
                if (isOnUppercaseSequence) {
                    result.append(",'");
                    isOnUppercaseSequence = false;
                }
                if (isOnNumberSequence) {
                    result.append(numberSegment).append(";");
                    numberSegment.setLength(0);
                    isOnNumberSequence = false;
                }

                int j = i + 1;
                while (j < length && Character.isLowerCase(input.charAt(j))) {
                    j++;
                }
                String lowerCaseSegment = input.substring(i, j);
                result.append(lowerCaseSegment);
                i = j - 1;
            } else if (Character.isDigit(currentChar) && !isOnNumberSequence) {

                int j = i + 1;
                while (j < length && Character.isDigit(input.charAt(j))) {
                    j++;
                }
                String digitSegment = input.substring(i, j);

                numberSegment.append("#");
                for (char digit : digitSegment.toCharArray()) {
                    numberSegment.append(digitToAscii.getOrDefault(digit, digit));
                }

                isOnNumberSequence = true;
                i = j - 1;
            }else if (isOnNumberSequence && Character.isWhitespace(currentChar)) {
                int j = i+1;
                if (Character.isDigit(input.charAt(j))) {
                    while (j < length && Character.isDigit(input.charAt(j))) {
                        j++;

                    }
                    numberSegment.append("\"");
                    String digitSegment = input.substring(i + 1, j);

                    for (char digit : digitSegment.toCharArray()) {
                        numberSegment.append(digitToAscii.getOrDefault(digit, ' '));
                    }
                    i = j - 1;
                }else{
                    result.append(numberSegment).append(currentChar);
                    numberSegment.setLength(0);
                    isOnNumberSequence = false;
                }

            } else if (isOnNumberSequence && currentChar == ',') {
                int j = i+1;
                if (Character.isDigit(input.charAt(j))) {

                    while (j < length && Character.isDigit(input.charAt(j))) {
                        j++;
                    }
                    String digitSegment = input.substring(i+1, j);

                    numberSegment.append("1");
                    for (char digit : digitSegment.toCharArray()) {
                        numberSegment.append(digitToAscii.getOrDefault(digit, digit));
                    }

                    i = j - 1;
                }else{
                    result.append(numberSegment).append(currentChar);
                    numberSegment.setLength(0);
                    isOnNumberSequence = false;
                }
            }
            else {
                if(isOnNumberSequence){
                    result.append(numberSegment);
                    numberSegment.setLength(0);
                    isOnNumberSequence = false;
                }else if(isOnUppercaseSequence){
                    isOnUppercaseSequence = false;
                }

                String mappedValue = symbolToAscii.getOrDefault(currentChar, " ");
                result.append(mappedValue);


            }

        }

        if(isOnNumberSequence){
            result.append(numberSegment);
            numberSegment.setLength(0);
        }
        return result.toString();
    }

    public static ArrayList<String> wrapAscii(String input, int maxWidth, int maxRows) {
        ArrayList<String> wrappedLines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        Pattern pattern = Pattern.compile("\\S+|\\s+");
        Matcher matcher = pattern.matcher(input);
        int currentRow = 1;

        while (matcher.find()) {
            String part = matcher.group();

            while (part.length() > maxWidth - currentLine.length()) {
                int spaceLeft = maxWidth - currentLine.length();
                currentLine.append(part.substring(0, spaceLeft));
                wrappedLines.add(addZerosToMaxWidth(currentLine.toString().stripTrailing(), maxWidth));
                part = part.substring(spaceLeft);
                currentLine.setLength(0);
                currentRow++;
                if (currentRow > maxRows) {
                    return wrappedLines;
                }
            }

            if (currentLine.length() + part.length() > maxWidth) {
                wrappedLines.add(addZerosToMaxWidth(currentLine.toString().stripTrailing(), maxWidth));
                currentLine.setLength(0);
                currentRow++;
                if (currentRow > maxRows) {
                    return wrappedLines;
                }
            }

            currentLine.append(part);
        }

        if (currentLine.length() > 0 && currentRow <= maxRows) {
            wrappedLines.add(addZerosToMaxWidth(currentLine.toString().stripTrailing(), maxWidth));
        }

        return wrappedLines;
    }

    public static String addZerosToMaxWidth(String line, int maxWidth) {
        if (line.length() < maxWidth) {
            StringBuilder paddedLine = new StringBuilder(line);
            while (paddedLine.length() < maxWidth) {
                paddedLine.append(" ");
            }
            return paddedLine.toString();
        }
        return line;
    }

    public static ArrayList<String> brailleletter(ArrayList<String> AsciiCode) {
        ArrayList<String> brailleList = new ArrayList<>();
        for (String ascii : AsciiCode) {
            String braille = convertToBraille(ascii,0,2);
            brailleList.add(braille);
            String braillemid = convertToBraille(ascii,2,4);
            brailleList.add(braillemid);
            String braillelast = convertToBraille(ascii,4,6);
            brailleList.add(braillelast);
        }
        return brailleList;
    }

    public static String convertToBraille(String ascii, int start, int end){
        StringBuilder line = new StringBuilder();
        for (char asciiLetter : ascii.toCharArray()) {
            String brailleBinary = CharToBraille.get(asciiLetter);

            if (brailleBinary == null) {
                System.out.println("Null encountered for asciiLetter: '" + asciiLetter + "'");
                break;
            } else {
                String BraillePart= brailleBinary.substring(start, end);
                line.append(BraillePart);
            }
        }
        return line.toString();
    }
}