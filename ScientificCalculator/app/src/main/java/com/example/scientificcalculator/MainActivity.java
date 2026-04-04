package com.example.scientificcalculator;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button b1, b2, b3, b4, b5, b6, b7, b8, b9, b0;
    Button bdot;
    Button bpi;

    Button bequal;
    Button bc;
    Button bac;


    Button bplus, bmin, bmul, bdiv;


    Button bb1, bb2;

    Button binv;
    Button bsqrt;
    Button bsquare;
    Button bfact;
    Button bln;
    Button blog;
    Button btan;
    Button bcos;
    Button bsin;

    TextView tvmain;
    TextView tvsec;


    final String PI_VALUE = "3.14159265358979";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvmain = findViewById(R.id.tvmain);
        tvsec  = findViewById(R.id.tvsec);


        b0 = findViewById(R.id.b0); b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2); b3 = findViewById(R.id.b3);
        b4 = findViewById(R.id.b4); b5 = findViewById(R.id.b5);
        b6 = findViewById(R.id.b6); b7 = findViewById(R.id.b7);
        b8 = findViewById(R.id.b8); b9 = findViewById(R.id.b9);


        bdot   = findViewById(R.id.bdot);
        bpi    = findViewById(R.id.bpi);
        bequal = findViewById(R.id.bequal);


        bplus = findViewById(R.id.bplus);
        bmin  = findViewById(R.id.bmin);
        bmul  = findViewById(R.id.bmul);
        bdiv  = findViewById(R.id.bdiv);


        binv    = findViewById(R.id.binv);
        bsqrt   = findViewById(R.id.bsqrt);
        bsquare = findViewById(R.id.bsquare);
        bfact   = findViewById(R.id.bfact);
        bln     = findViewById(R.id.bln);
        blog    = findViewById(R.id.blog);
        btan    = findViewById(R.id.btan);
        bcos    = findViewById(R.id.bcos);
        bsin    = findViewById(R.id.bsin);


        bb1 = findViewById(R.id.bb1);
        bb2 = findViewById(R.id.bb2);
        bc  = findViewById(R.id.bc);
        bac = findViewById(R.id.bac);


        View.OnClickListener appendLabel = v -> {
            Button btn = (Button) v;
            tvmain.append(btn.getText().toString());
        };


        b0.setOnClickListener(appendLabel);
        b1.setOnClickListener(appendLabel);
        b2.setOnClickListener(appendLabel);
        b3.setOnClickListener(appendLabel);
        b4.setOnClickListener(appendLabel);
        b5.setOnClickListener(appendLabel);
        b6.setOnClickListener(appendLabel);
        b7.setOnClickListener(appendLabel);
        b8.setOnClickListener(appendLabel);
        b9.setOnClickListener(appendLabel);


        bdot.setOnClickListener(v -> tvmain.append("."));



        bac.setOnClickListener(v -> {
            tvmain.setText("");
            tvsec.setText("");
        });


        bc.setOnClickListener(v -> {
            String val = tvmain.getText().toString();
            if (!val.isEmpty()) {
                tvmain.setText(val.substring(0, val.length() - 1));
            }
        });


        bplus.setOnClickListener(v -> tvmain.append("+"));
        bmin.setOnClickListener(v  -> tvmain.append("-"));
        bmul.setOnClickListener(v  -> tvmain.append("×"));
        bdiv.setOnClickListener(v  -> tvmain.append("÷"));


        bb1.setOnClickListener(v -> tvmain.append("("));
        bb2.setOnClickListener(v -> tvmain.append(")"));


        bpi.setOnClickListener(v -> {
            tvsec.setText("π =");
            tvmain.append(PI_VALUE);
        });


        bsin.setOnClickListener(v -> tvmain.append("sin"));
        bcos.setOnClickListener(v -> tvmain.append("cos"));
        btan.setOnClickListener(v -> tvmain.append("tan"));


        blog.setOnClickListener(v -> tvmain.append("log"));
        bln.setOnClickListener(v  -> tvmain.append("ln"));


        bsqrt.setOnClickListener(v -> {
            try {
                double val    = Double.parseDouble(tvmain.getText().toString());
                double result = Math.sqrt(val);
                tvsec.setText("√" + formatResult(val));
                tvmain.setText(formatResult(result));
            } catch (Exception e) {
                tvmain.setText("Error");
            }
        });


        bsquare.setOnClickListener(v -> {
            try {
                double val    = Double.parseDouble(tvmain.getText().toString());
                double result = val * val;
                tvsec.setText(formatResult(val) + "²");
                tvmain.setText(formatResult(result));
            } catch (Exception e) {
                tvmain.setText("Error");
            }
        });


        binv.setOnClickListener(v -> tvmain.append("^(-1)"));


        bfact.setOnClickListener(v -> {
            try {
                int val    = Integer.parseInt(tvmain.getText().toString());
                long result = factorial(val);
                tvsec.setText(val + "!");
                tvmain.setText(String.valueOf(result));
            } catch (Exception e) {
                tvmain.setText("Error");
            }
        });


        bequal.setOnClickListener(v -> {
            try {
                String expression = tvmain.getText().toString();

                String normalized = expression
                        .replace('÷', '/')
                        .replace('×', '*');
                double result = eval(normalized);
                tvsec.setText(expression + " =");
                tvmain.setText(formatResult(result));
            } catch (Exception e) {
                tvmain.setText("Error");
            }
        });
    }


    private String formatResult(double value) {
        if (value == (long) value) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }


    long factorial(int n) {
        if (n < 0)  throw new IllegalArgumentException("Negative factorial");
        return (n == 0 || n == 1) ? 1L : (long) n * factorial(n - 1);
    }


    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;


            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }


            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) { nextChar(); return true; }
                return false;
            }

            double parse() {
                nextChar();
                return parseExpression();
            }


            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            // Handles * and / (medium precedence)
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }


            double parseFactor() {
                if (eat('+')) return  parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;

                if (eat('(')) {

                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {

                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') {

                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();

                    switch (func) {
                        case "sqrt": x = Math.sqrt(x);                   break;
                        case "sin":  x = Math.sin(Math.toRadians(x));    break;
                        case "cos":  x = Math.cos(Math.toRadians(x));    break;
                        case "tan":  x = Math.tan(Math.toRadians(x));    break;
                        case "log":  x = Math.log10(x);                  break;
                        case "ln":   x = Math.log(x);                    break;
                        default: throw new RuntimeException("Unknown function: " + func);
                    }
                } else {
                    throw new RuntimeException("Unexpected character: " + (char) ch);
                }


                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }
}
