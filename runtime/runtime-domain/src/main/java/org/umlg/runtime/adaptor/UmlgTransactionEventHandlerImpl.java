package org.umlg.runtime.adaptor;

import org.umlg.runtime.collection.UmlgCollection;
import org.umlg.runtime.domain.AssociationClassNode;
import org.umlg.runtime.domain.UmlgNode;
import org.umlg.runtime.notification.ChangeHolder;
import org.umlg.runtime.notification.NotificationListener;
import org.umlg.runtime.validation.UmlgConstraintViolation;
import org.umlg.runtime.validation.UmlgConstraintViolationException;

import java.util.List;
import java.util.Map;

/**
 * This class validates what is being committed.
 * On every UmlgNode is calls validateMultiplicities.
 * futher it validates that a non root class has one and only one composite parent
 */
public class UmlgTransactionEventHandlerImpl implements UmlgTransactionEventHandler {

    //This is needed in Neo4j when starting up the graph.
    //In Particular when creating indexes and doing schema updates the transaction count should not be updated as updates
    // are not allowed when doing schema updates.
    private boolean bypass = false;

    public UmlgTransactionEventHandlerImpl() {
        super();
    }

    @Override
    public void beforeCommit() {
        try {
            if (!this.bypass && UMLG.get() != null && !UMLG.get().isInBatchMode()) {
                TransactionThreadVar.clear();
//                ((UmlgAdminGraph) UMLG.get()).incrementTransactionCount();
                List<UmlgNode> entities = TransactionThreadEntityVar.get();
                for (UmlgNode umlgNode : entities) {
                    if (umlgNode instanceof AssociationClassNode) {
                        if (!umlgNode.getVertex().property(UmlgCollection.ASSOCIATION_CLASS_EDGE_ID).isPresent()) {
                            throw new IllegalStateException(String.format("AssociationClass entity %s %s property %s is not set. This happens when the association end is a Set and was already present.", umlgNode.getClass().getSimpleName(), umlgNode.getId(), UmlgCollection.ASSOCIATION_CLASS_EDGE_ID));
                        }
                    }
                    List<UmlgConstraintViolation> requiredConstraintViolations = umlgNode.validateMultiplicities();
                    requiredConstraintViolations.addAll(umlgNode.checkClassConstraints());
                    if (!requiredConstraintViolations.isEmpty()) {
                        throw new UmlgConstraintViolationException(requiredConstraintViolations);
                    }
                    if (!umlgNode.isTinkerRoot() && (!umlgNode.hasOnlyOneCompositeParent() || umlgNode.getOwningObject() == null)) {
                        throw new IllegalStateException(String.format("Entity %s %s does not have a composite owner", umlgNode.getClass().getSimpleName(), umlgNode.getId()));
                    }
                    umlgNode.doBeforeCommit();
                }
                for (Map.Entry<NotificationListener, List<ChangeHolder>> notificationListenerSetEntry : TransactionThreadNotificationVar.get().entrySet()) {
                    NotificationListener notificationListener = notificationListenerSetEntry.getKey();
                    List<ChangeHolder> changeHolders = notificationListenerSetEntry.getValue();
                    for (ChangeHolder changeHolder : changeHolders) {
                        notificationListener.notifyChanged(
                                NotificationListener.COMMIT_TYPE.BEFORE_COMMIT,
                                changeHolder.getUmlgNode(),
                                changeHolder.getUmlgRuntimeProperty(),
                                changeHolder.getChangeType(),
                                changeHolder.getValue());
                    }
                }
            }
        } finally {
            TransactionThreadEntityVar.remove();
            TransactionThreadMetaNodeVar.remove();
        }
    }

    @Override
    public void afterCommit() {
        try {
            for (Map.Entry<NotificationListener, List<ChangeHolder>> notificationListenerSetEntry : TransactionThreadNotificationVar.get().entrySet()) {
                NotificationListener notificationListener = notificationListenerSetEntry.getKey();
                List<ChangeHolder> changeHolders = notificationListenerSetEntry.getValue();
                for (ChangeHolder changeHolder : changeHolders) {
                    new Thread(() -> {
                        notificationListener.notifyChanged(
                                NotificationListener.COMMIT_TYPE.AFTER_COMMIT,
                                changeHolder.getUmlgNode(),
                                changeHolder.getUmlgRuntimeProperty(),
                                changeHolder.getChangeType(),
                                changeHolder.getValue());
                    }, changeHolder.getUmlgNode().getQualifiedName() + changeHolder.getUmlgRuntimeProperty().getQualifiedName()).start();
                }
            }
        } finally {
            TransactionThreadNotificationVar.remove();
        }
    }

    public void setBypass(boolean bypass) {
        this.bypass = bypass;
    }

}
