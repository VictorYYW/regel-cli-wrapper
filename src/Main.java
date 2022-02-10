import edu.stanford.nlp.sempre.Builder;
import edu.stanford.nlp.sempre.Derivation;
import edu.stanford.nlp.sempre.Master;
import edu.stanford.nlp.sempre.Parser;
import edu.stanford.nlp.sempre.Session;
import fig.exec.Execution;
import resnax.so.Benchmark;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Main implements Runnable {
    public int beam = 200;

    private static final String BENCHMARK_FILE = "tmp";

    private String parse_nl(String utterance) {
        Builder builder = new Builder();
        builder.build();

        Master master = new Master(builder);

        Session session = master.getSession("stdin");
        Master.Response response = master.processQuery(session, utterance);

        List<Derivation> derivs = response.ex.getPredDerivations();
        String derivString = ((derivs.get(0)).value).toString();
        String subDeriv = "";
        if (derivString.contains("\"")) {
            subDeriv = derivString.split("\"")[1];
        } else {
            subDeriv = derivString.split(" ")[1];
            subDeriv = subDeriv.substring(0, subDeriv.indexOf(")"));
        }
        return subDeriv;
    }

    private void read_lines_to_list(BufferedReader reader, List<String> lines) {
        try {
            String line = reader.readLine();
            for (; !line.isEmpty(); line = reader.readLine()) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IO: read lines");
        }
    }

    @Override
    public void run() {
        // get utterance from console
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please give a description of the regex you want to synthesize in English in one line:");
        String utterance = null;
        try {
            utterance = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IO: natural language description");
        }

        // parse utterance and get regex sketch
        String sketch = parse_nl(utterance);

        System.out.println("Please provide examples in separate lines:\n" +
                        "in the form of \"example\",+ or \"example\",-\n" +
                        "for positive examples and negative examples respectively:"
                );
        // get original set of examples
        List<String> lines = new ArrayList<>();
        read_lines_to_list(reader, lines);
        Benchmark benchmark = Benchmark.read(lines, sketch);
        benchmark.run_interactive();

        while (true) {
            System.out.println("Is the regex correct? Y/n");
            try {
                String ans = reader.readLine();
                if (ans.isEmpty() || ans.equals("y") || ans.equals("Y")) {
                    break;
                } else if (ans.equals("n") || ans.equals("N")) {
                    System.out.println("Please provide more examples:");
                    read_lines_to_list(reader, lines);
                    benchmark = Benchmark.read(lines, sketch);
                    benchmark.run_interactive();
                } else {
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("IO: regex check");
            }
        }

        System.out.println("regex found, exiting..");
    }

    public static void main(String[] args) {
        System.out.println("This is a simple wrapper for regular expression synthesizer - REGEL.");
        Main t = new Main();
        Execution.run(args, "Main", t, Master.getOptionsParser());
    }
}
