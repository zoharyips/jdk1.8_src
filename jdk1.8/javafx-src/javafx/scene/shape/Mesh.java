/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package javafx.scene.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.sg.prism.NGTriangleMesh;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import sun.util.logging.PlatformLogger;

/**
 * Base class for representing a 3D geometric surface.
 *
 * Note that this is a conditional feature. See
 * {@link javafx.application.ConditionalFeature#SCENE3D ConditionalFeature.SCENE3D}
 * for more information.
 *
 * @since JavaFX 8.0
 */
public abstract class Mesh {

    protected Mesh() {
        if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
            String logname = Mesh.class.getName();
            PlatformLogger.getLogger(logname).warning("System can't support "
                                                      + "ConditionalFeature.SCENE3D");
        }
    }

    // Mesh isn't a Node. It can't use the standard dirtyBits pattern that is
    // in Node
    // TODO: 3D - Material and Mesh have similar pattern. We should look into creating
    // a "NodeComponent" class if more non-Node classes are needed.

    // Material isn't a Node. It can't use the standard dirtyBits pattern that is
    // in Node
    private final BooleanProperty dirty = new SimpleBooleanProperty(true);

    final boolean isDirty() {
        return dirty.getValue();
    }

    void setDirty(boolean value) {
        dirty.setValue(value);
    }

    final BooleanProperty dirtyProperty() {
        return dirty;
    }

    // We only support one type of mesh for FX 8.
    abstract NGTriangleMesh getPGMesh();
    abstract void impl_updatePG();

    abstract BaseBounds computeBounds(BaseBounds b);

    /**
     * Picking implementation.
     * @param pickRay The pick ray
     * @param pickResult The pick result to be updated (if a closer intersection is found)
     * @param candidate The Node that owns this mesh to be filled in the pick
     *                  result in case a closer intersection is found
     * @param cullFace The cull face of the node that owns this mesh
     * @param reportFace Whether to report the hit face
     * @return true if the pickRay intersects this mesh (regardless of whether
     *              the pickResult has been updated)
     *
     * @treatAsPrivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    abstract protected boolean impl_computeIntersects(PickRay pickRay,
            PickResultChooser pickResult, Node candidate, CullFace cullFace,
            boolean reportFace);
}
