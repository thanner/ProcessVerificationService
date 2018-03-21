package br.edu.ufrgs.inf.bpm;

import org.yawlfoundation.yawl.elements.*;
import org.yawlfoundation.yawl.elements.data.YVariable;
import org.yawlfoundation.yawl.engine.YNetData;
import org.yawlfoundation.yawl.schema.YSchemaVersion;
import org.yawlfoundation.yawl.util.YVerificationHandler;
import org.yawlfoundation.yawl.util.YVerificationMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {

    private static YCondition _aCondition;
    private static YTask _validTask;
    private static YTask _invalidTask;
    private static YTask _invalidTask2;
    private static YTask _needsPredicateString;
    private static YTask _needsNoPredicateString;

    private static YSpecification spec;
    private static YNet deadNet;
    private static YFlow flowAux;

    public static void main(String[] args) {
        setup();
        YVerificationHandler handler = new YVerificationHandler();
        List<YTask> yTasks = new ArrayList<>();
        yTasks.add(_validTask);
        yTasks.add(_invalidTask);
        yTasks.add(_invalidTask2);
        yTasks.add(_needsPredicateString);
        yTasks.add(_needsNoPredicateString);

        for (YTask yTask : yTasks) {
            System.out.println(yTask.getName());
            yTask.verify(handler);
            if (handler.hasMessages()) {
                for (YVerificationMessage msg : handler.getMessages()) {
                    System.out.println("\t" + msg.getMessage());
                }
            }
            yTask.getRemoveSet();
        }
    }

    public static void setup() {
        try {
            spec = new YSpecification("");
            spec.setVersion(YSchemaVersion.Beta2);

            YVariable yVariable = new YVariable(null);
            yVariable.setName("stubList");
            yVariable.setUntyped(true);
            yVariable.setInitialValue("<stub/><stub/><stub/>");

            deadNet = new YNet("aNetName", spec);
            YNetData casedata = new YNetData();
            deadNet.initializeDataStore(null, casedata);
            deadNet.setLocalVariable(yVariable);
            deadNet.initialise(null);

            _aCondition = new YCondition("c1", deadNet);

            createValidTask();

            // _invalidTask

            _invalidTask = new YAtomicTask("et2", 21, 32, null);
            _invalidTask.setName("_invalidTask");

            _invalidTask.setUpMultipleInstanceAttributes("0", "-1", "-1", "dfsdfsdf");

            // _invalidTask2

            _invalidTask2 = new YAtomicTask("et3", YTask._AND, YTask._XOR, null);
            _invalidTask2.setName("_invalidTask2");

            flowAux = new YFlow(_aCondition, _invalidTask2);
            _invalidTask2.addPreset(flowAux);

            flowAux = new YFlow(_invalidTask2, _aCondition);
            _invalidTask2.addPostset(flowAux);

            // _needsPredicateString

            _needsPredicateString = new YAtomicTask("et3", YAtomicTask._XOR, YAtomicTask._OR, deadNet);
            _needsPredicateString.setName("_needsPredicateString");

            flowAux = new YFlow(_needsPredicateString, _aCondition);
            _needsPredicateString.addPostset(flowAux);

            flowAux = new YFlow(_aCondition, _needsPredicateString);
            _needsPredicateString.addPreset(flowAux);

            // _needsNoPredicateString

            _needsNoPredicateString = new YAtomicTask("et4", YAtomicTask._AND, YAtomicTask._AND, deadNet);
            _needsNoPredicateString.setName("_needsNoPredicateString");

            flowAux = new YFlow(_needsNoPredicateString, _aCondition);
            flowAux.setXpathPredicate("not valid xpath");
            _needsNoPredicateString.addPostset(flowAux);

            flowAux = new YFlow(_aCondition, _needsNoPredicateString);
            _needsNoPredicateString.addPreset(flowAux);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createValidTask() {
        _validTask = new YAtomicTask("et1", YTask._AND, YTask._OR, deadNet);
        _validTask.setName("_validTask");

        flowAux = new YFlow(_aCondition, _validTask);
        _aCondition.addPostset(flowAux);

        flowAux = new YFlow(_validTask, _aCondition);
        flowAux.setIsDefaultFlow(true);
        flowAux.setXpathPredicate("random()");

        _validTask.addPostset(flowAux);
        _validTask.setUpMultipleInstanceAttributes("3", "3", "3", "static");

        YDecomposition descriptor = new YAWLServiceGateway("Wash floor", spec);
        _validTask.setDecompositionPrototype(descriptor);

        Map map = new HashMap();
        map.put("stub", "/data/stubList");

        _validTask.setDataMappingsForTaskStarting(map);
        _validTask.setMultiInstanceInputDataMappings("stub", "for $d in /stubList/* return $d");
    }

}
