import edu.stanford.nlp.sempre.Builder;
import edu.stanford.nlp.sempre.Derivation;
import edu.stanford.nlp.sempre.Master;
import edu.stanford.nlp.sempre.Parser;
import edu.stanford.nlp.sempre.Session;
import fig.exec.Execution;
import resnax.so.Benchmark;

import java.io.*;
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

    private void read_lines_to_buffer(BufferedReader reader, BufferedWriter writer) throws IOException{
        String line = reader.readLine();
        for (; !line.isEmpty(); line = reader.readLine()) {
            writer.write(line);
            writer.write("\n");
        }
    }

    @Override
    public void run() {
        // get utterance from console
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please give a description of the regex you want to synthesize in English in one line:");
        String utterance = null;
        try {
            utterance = bf.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IO: natural language description");
        }

        // parse utterance and get regex sketch
        String sketch = parse_nl(utterance);

        System.out.println("Please provide examples:\n");
        // get original set of examples and build benchmark file
        try {
            BufferedWriter benchmark_writer = new BufferedWriter(new FileWriter(BENCHMARK_FILE));
            benchmark_writer.write("natural language\n\n");
            read_lines_to_buffer(bf, benchmark_writer);
            benchmark_writer.write("\nna");
            benchmark_writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IO: examples");
        }


        Benchmark benchmark = Benchmark.read(BENCHMARK_FILE, "log", sketch, "0");
        benchmark.run_interactive();
    }

    public static void main(String[] args) {
        System.out.println("This is a simple wrapper for regular expression synthesizer - REGEL.");
        Main t = new Main();
        Execution.run(args, "Main", t, Master.getOptionsParser());
    }
}
