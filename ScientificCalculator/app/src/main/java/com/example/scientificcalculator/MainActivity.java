package com.example.scientificcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button b1,b2,b3,b4,b5,b6,b7,b8,b9,b0,bdot,bpi,bequal,
            bplus,bmin,bmul,bdiv,binv,bsqrt,bsquare,bfact,
            bln,blog,btan,bcos,bsin,bb1,bb2,bc,bac;

    TextView tvmain,tvsec;
    String pi = "3.14159265";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TextViews
        tvmain = findViewById(R.id.tvmain);
        tvsec = findViewById(R.id.tvsec);

        // Buttons
        b1 = findViewById(R.id.b1); b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3); b4 = findViewById(R.id.b4);
        b5 = findViewById(R.id.b5); b6 = findViewById(R.id.b6);
        b7 = findViewById(R.id.b7); b8 = findViewById(R.id.b8);
        b9 = findViewById(R.id.b9); b0 = findViewById(R.id.b0);

        bdot = findViewById(R.id.bdot);
        bpi = findViewById(R.id.bpi);
        bequal = findViewById(R.id.bequal);

        bplus = findViewById(R.id.bplus);
        bmin = findViewById(R.id.bmin);
        bmul = findViewById(R.id.bmul);
        bdiv = findViewById(R.id.bdiv);

        binv = findViewById(R.id.binv);
        bsqrt = findViewById(R.id.bsqrt);
        bsquare = findViewById(R.id.bsquare);
        bfact = findViewById(R.id.bfact);

        bln = findViewById(R.id.bln);
        blog = findViewById(R.id.blog);
        btan = findViewById(R.id.btan);
        bcos = findViewById(R.id.bcos);
        bsin = findViewById(R.id.bsin);

        bb1 = findViewById(R.id.bb1);
        bb2 = findViewById(R.id.bb2);
        bc = findViewById(R.id.bc);
        bac = findViewById(R.id.bac);

        // Number buttons (optimized)
        View.OnClickListener numberClick = v -> {
            Button b = (Button) v;
            tvmain.setText(tvmain.getText() + b.getText().toString());
        };

        b0.setOnClickListener(numberClick);
        b1.setOnClickListener(numberClick);
        b2.setOnClickListener(numberClick);
        b3.setOnClickListener(numberClick);
        b4.setOnClickListener(numberClick);
        b5.setOnClickListener(numberClick);
        b6.setOnClickListener(numberClick);
        b7.setOnClickListener(numberClick);
        b8.setOnClickListener(numberClick);
        b9.setOnClickListener(numberClick);

        bdot.setOnClickListener(v -> tvmain.append("."));

        // Clear all
        bac.setOnClickListener(v -> {
            tvmain.setText("");
            tvsec.setText("");
        });

        // Backspace (FIXED crash)
        bc.setOnClickListener(v -> {
            String val = tvmain.getText().toString();
            if (!val.isEmpty()) {
                tvmain.setText(val.substring(0, val.length() - 1));
            }
        });

        // Operators
        bplus.setOnClickListener(v -> tvmain.append("+"));
        bmin.setOnClickListener(v -> tvmain.append("-"));
        bmul.setOnClickListener(v -> tvmain.append("×"));
        bdiv.setOnClickListener(v -> tvmain.append("÷"));

        // Brackets
        bb1.setOnClickListener(v -> tvmain.append("("));
        bb2.setOnClickListener(v -> tvmain.append(")"));

        // Pi
        bpi.setOnClickListener(v -> {
            tvsec.setText("π");
            tvmain.append(pi);
        });

        // Functions
        bsin.setOnClickListener(v -> tvmain.append("sin"));
        bcos.setOnClickListener(v -> tvmain.append("cos"));
        btan.setOnClickListener(v -> tvmain.append("tan"));
        bln.setOnClickListener(v -> tvmain.append("ln"));
        blog.setOnClickListener(v -> tvmain.append("log"));

        // Square root
        bsqrt.setOnClickListener(v -> {
            try {
                double val = Double.parseDouble(tvmain.getText().toString());
                double result = Math.sqrt(val);
                tvmain.setText(String.valueOf(result));
                tvsec.setText("√" + val);
            } catch (Exception e) {
                tvmain.setText("Error");
            }
        });

        // Square
        bsquare.setOnClickListener(v -> {
            try {
                double val = Double.parseDouble(tvmain.getText().toString());
                double result = val * val;
                tvmain.setText(String.valueOf(result));
                tvsec.setText(val + "²");
            } catch (Exception e) {
                tvmain.setText("Error");
            }
        });

        // Inverse
        binv.setOnClickListener(v -> tvmain.append("^(-1)"));

        // Factorial
        bfact.setOnClickListener(v -> {
            try {
                int val = Integer.parseInt(tvmain.getText().toString());
                tvmain.setText(String.valueOf(factorial(val)));
                tvsec.setText(val + "!");
            } catch (Exception e) {
                tvmain.setText("Error");
            }
        });

        // Equal
        bequal.setOnClickListener(v -> {
            try {
                String val = tvmain.getText().toString();
                String replaced = val.replace('÷','/').replace('×','*');
                double result = eval(replaced);
                tvmain.setText(String.valueOf(result));
                tvsec.setText(val);
            } catch (Exception e) {
                tvmain.setText("Error");
            }
        });
    }

    // Factorial
    int factorial(int n) {
        return (n == 0 || n == 1) ? 1 : n * factorial(n - 1);
    }

    // Expression evaluator
    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
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
                        case "sqrt": x = Math.sqrt(x); break;
                        case "sin": x = Math.sin(Math.toRadians(x)); break;
                        case "cos": x = Math.cos(Math.toRadians(x)); break;
                        case "tan": x = Math.tan(Math.toRadians(x)); break;
                        case "log": x = Math.log10(x); break;
                        case "ln": x = Math.log(x); break;
                        default: throw new RuntimeException("Unknown: " + func);
                    }
                } else {
                    throw new RuntimeException("Error");
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }


}