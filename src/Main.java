import edu.stanford.nlp.sempre.Builder;
import edu.stanford.nlp.sempre.Derivation;
import edu.stanford.nlp.sempre.Master;
import edu.stanford.nlp.sempre.Parser;
import edu.stanford.nlp.sempre.Session;
import fig.exec.Execution;


public class Main implements Runnable {
    public int beam = 200;
    public String parse_nl(String utterance) {
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
//        for (int i = 0; i < derivs.size(); i++) {
//            String derivString = ((derivs.get(i)).value).toString();
//            System.out.print(derivString);
//        }
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

        // get original set of examples
    }

    public static void main(String[] args) {
        System.out.println("This is a simple wrapper for regular expression synthesizer - REGEL.");

        // find the sketch
        Main t = new Main();
        Execution.run(args, "Main", t, Master.getOptionsParser());
    }
}
