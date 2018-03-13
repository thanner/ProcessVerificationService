package br.edu.ufrgs.inf.bpm;

import org.yawlfoundation.yawl.elements.*;
import org.yawlfoundation.yawl.elements.data.YVariable;
import org.yawlfoundation.yawl.engine.YNetData;
import org.yawlfoundation.yawl.schema.YSchemaVersion;
import org.yawlfoundation.yawl.util.YVerificationHandler;
import org.yawlfoundation.yawl.util.YVerificationMessage;

import java.util.HashMap;
import java.util.Map;

public class App {

    private static YCondition _aCondition;
    private static YTask _validTask;
    private static YTask _invalidTask;
    private static YTask _needsPredicateString;
    private static YTask _needsNoPredicateString;
    private static YTask _invalidTask2;

    public static void main(String[] args) {
        setup();

        YVerificationHandler handler = new YVerificationHandler();
        _validTask.verify(handler);
        if (handler.hasMessages()) {
            for (YVerificationMessage msg : handler.getMessages()) {
                System.out.println(msg.getMessage());
            }
        }
        _validTask.getRemoveSet();
    }

    public static void setup() {
        try {
            YSpecification spec = new YSpecification("");
            spec.setVersion(YSchemaVersion.Beta2);
            YNet deadNet = new YNet("aNetName", spec);
            YVariable v = new YVariable(null);
            v.setName("stubList");
            v.setUntyped(true);
            v.setInitialValue("<stub/><stub/><stub/>");
            YNetData casedata = new YNetData();
            deadNet.initializeDataStore(null, casedata);
            deadNet.setLocalVariable(v);
            deadNet.initialise(null);
            _aCondition = new YCondition("c1", deadNet);
            _validTask = new YAtomicTask("et1", YTask._AND, YTask._OR, deadNet);
            YFlow f = new YFlow(_aCondition, _validTask);
            _aCondition.addPostset(f);
            Map map = new HashMap();
            map.put("stub", "/data/stubList");
            f = new YFlow(_validTask, _aCondition);
            f.setIsDefaultFlow(true);
            f.setXpathPredicate("random()");
            _validTask.addPostset(f);
            _validTask.setUpMultipleInstanceAttributes("3", "3", "3", "static");

            YDecomposition descriptor = new YAWLServiceGateway("Wash floor", spec);
            _validTask.setDecompositionPrototype(descriptor);
            _invalidTask = new YAtomicTask("et2", 21, 32, null);
            _invalidTask.setUpMultipleInstanceAttributes("0", "-1", "-1", "dfsdfsdf");
            _validTask.setDataMappingsForTaskStarting(map);
            _validTask.setMultiInstanceInputDataMappings("stub", "for $d in /stubList/* return $d");
            _invalidTask2 = new YAtomicTask("et3", YTask._AND, YTask._XOR, null);
            f = new YFlow(_invalidTask2, _aCondition);
            _invalidTask2.addPostset(f);
            f = new YFlow(_aCondition, _invalidTask2);
            _invalidTask2.addPreset(f);
            _needsPredicateString = new YAtomicTask("et3", YAtomicTask._XOR, YAtomicTask._OR, deadNet);
            f = new YFlow(_needsPredicateString, _aCondition);
            _needsPredicateString.addPostset(f);
            f = new YFlow(_aCondition, _needsPredicateString);
            _needsPredicateString.addPreset(f);
            _needsNoPredicateString = new YAtomicTask("et4", YAtomicTask._AND, YAtomicTask._AND, deadNet);
            f = new YFlow(_needsNoPredicateString, _aCondition);
            f.setXpathPredicate("not valid xpath");
            _needsNoPredicateString.addPostset(f);
            f = new YFlow(_aCondition, _needsNoPredicateString);
            _needsNoPredicateString.addPreset(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
