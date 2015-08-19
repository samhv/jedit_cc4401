package org.incha.core;

import java.util.LinkedList;
import java.util.List;

import org.incha.core.jswingripples.JRipplesICModuleInterface;
import org.incha.core.jswingripples.JRipplesModuleInterface;
import org.incha.core.jswingripples.analysis.JRipplesModuleAnalysisDefaultImpactSetConnections;
import org.incha.core.jswingripples.eig.JSwingRipplesEIG;
import org.incha.core.jswingripples.parser.MethodGranularityDependencyBuilder;
import org.incha.core.jswingripples.parser.MethodGranularityDependencyBuilderPolymorphic;
import org.incha.core.jswingripples.rules.JRipplesModuleICChangePropagation;
import org.incha.core.jswingripples.rules.JRipplesModuleICChangePropagationRelaxed;
import org.incha.core.jswingripples.rules.JRipplesModuleICConceptLocationRelaxed;
import org.incha.core.jswingripples.rules.JRipplesModuleICDefaultConceptLocation;
import org.incha.core.jswingripples.rules.JRipplesModuleICImpactAnalysis;
import org.incha.core.jswingripples.rules.JRipplesModuleICImpactAnalysisRelaxed;



/**
 * Manages information about the type of Analysis made by JRipples, doesn't include the actual procedures.
 */
public class ModuleConfiguration {
    public static final int MODULE_IMPACT_ANALYSIS = 0;
    public static final int MODULE_IMPACT_ANALYSIS_RELAXED = 1;
    public static final int MODULE_CHANGE_PROPAGATION_RELAXED = 2;
    public static final int MODULE_CHANGE_PROPAGATION = 3;
    public static final int MODULE_CONCEPT_LOCATION = 4;
    public static final int MODULE_CONCEPT_LOCATION_RELAXED = 5;

    public static final int MODULE_DEPENDENCY_BUILDER = 0;
    public static final int MODULE_DEPENDENCY_BUILDER_WITH_POLYMORPHIC = 1;

    public static final int MODULE_VIEW_HIERARCHY = 0;
    public static final int MODULE_VIEW_TREE = 1;

    private int dependencyGraphModule;
    private int incrementalChange = MODULE_CONCEPT_LOCATION;
    private int analysis;

    /**
     * Default
     */
    public ModuleConfiguration() {
        super();
    }

    /**
     * @param type the dependencyGraphModule to set
     */
    public void setDependencyGraphModule(final int type) {
        this.dependencyGraphModule = type;
    }
    /**
     * @return the dependencyGraphModule
     */
    public int getDependencyGraphModule() {
        return dependencyGraphModule;
    }
    /**
     * @param moduleImpactAnalysisTitle
     */
    public void setIncrementalChange(final int type) {
        this.incrementalChange = type;
    }
    /**
     * @return the incremantealChange
     */
    public int getIncrementalChange() {
        return incrementalChange;
    }

    /**
     * @param typee
     */
    public void setAnalysis(final int type) {
        this.analysis = type;
    }
    /**
     * @return the analysis
     */
    public int getAnalysis() {
        return analysis;
    }

    public List<JRipplesModuleInterface> buildModules(
            final JSwingRipplesEIG eig, final JavaProject project) {
        final List<JRipplesModuleInterface> modules = new LinkedList<JRipplesModuleInterface>();

        modules.add(createDependencyBuilderModule(eig));
        modules.add(createIncrementalChangeModule(eig));

        //analysis
        if (isAnalysisDefaultImpactSetConnections()) {
            modules.add(new JRipplesModuleAnalysisDefaultImpactSetConnections(eig));
        }


        return modules;
    }

    /**
     * @return
     */
    public boolean isAnalysisDefaultImpactSetConnections() {
        return getIncrementalChange() != MODULE_CONCEPT_LOCATION;
    }

    /**
     * @param eig
     * @return
     */
    public JRipplesICModuleInterface createIncrementalChangeModule(
            final JSwingRipplesEIG eig) {
        return createIncrementalChangeModule(getIncrementalChange(), eig);
    }

    /**
     * @param eig
     * @return
     */
    public JRipplesModuleInterface createDependencyBuilderModule(
            final JSwingRipplesEIG eig) {
        return createDependencyBuilderModule(getDependencyGraphModule(), eig);
    }

    /**
     * @param moduleType
     * @param eig
     * @return
     */
    public static JRipplesModuleInterface createDependencyBuilderModule(
            final int moduleType, final JSwingRipplesEIG eig) {
        if (moduleType == MODULE_DEPENDENCY_BUILDER) {
            return new MethodGranularityDependencyBuilder(eig);
        } else {
            return new MethodGranularityDependencyBuilderPolymorphic(eig);
        }
    }

    /**
     * @param type
     * @param eig
     * @return
     */
    public static JRipplesICModuleInterface createIncrementalChangeModule(
            final int type, final JSwingRipplesEIG eig) {
        switch (type) {
            case MODULE_IMPACT_ANALYSIS:
                return new JRipplesModuleICImpactAnalysis(eig);
            case MODULE_IMPACT_ANALYSIS_RELAXED:
                return new JRipplesModuleICImpactAnalysisRelaxed(eig);
            case MODULE_CHANGE_PROPAGATION_RELAXED:
                return new JRipplesModuleICChangePropagationRelaxed(eig);
            case MODULE_CHANGE_PROPAGATION:
                return new JRipplesModuleICChangePropagation(eig);
            case MODULE_CONCEPT_LOCATION:
                return new JRipplesModuleICDefaultConceptLocation(eig);
            case MODULE_CONCEPT_LOCATION_RELAXED:
                return new JRipplesModuleICConceptLocationRelaxed(eig);
        }
        return null;
    }
}
