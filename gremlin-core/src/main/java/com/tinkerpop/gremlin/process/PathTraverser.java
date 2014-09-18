package com.tinkerpop.gremlin.process;

import com.tinkerpop.gremlin.structure.Edge;
import com.tinkerpop.gremlin.structure.MetaProperty;
import com.tinkerpop.gremlin.structure.Property;
import com.tinkerpop.gremlin.structure.Vertex;
import com.tinkerpop.gremlin.structure.util.detached.DetachedEdge;
import com.tinkerpop.gremlin.structure.util.detached.DetachedMetaProperty;
import com.tinkerpop.gremlin.structure.util.detached.DetachedPath;
import com.tinkerpop.gremlin.structure.util.detached.DetachedProperty;
import com.tinkerpop.gremlin.structure.util.detached.DetachedVertex;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class PathTraverser<T> extends SimpleTraverser<T> {

    private Path path = new Path();

    private PathTraverser() {
        super();
    }

    public PathTraverser(final T t, final Traversal.SideEffects sideEffects) {
        super(t, sideEffects);
    }

    public PathTraverser(final String as, final T t, final Traversal.SideEffects sideEffects) {
        super(t, sideEffects);
        this.path.add(as, t);
    }

    @Override
    public Path getPath() {
        return this.path;
    }

    @Override
    public void setPath(final Path path) {
        this.path = path;
    }

    @Override
    public <R> PathTraverser<R> makeChild(final String label, final R r) {
        final PathTraverser<R> traverser = new PathTraverser<>(r, this.sideEffects);
        traverser.loops = this.loops;
        traverser.path.add(this.path);
        traverser.path.add(label, r);
        traverser.future = this.future;
        return traverser;
    }

    @Override
    public PathTraverser<T> makeSibling() {
        final PathTraverser<T> traverser = new PathTraverser<>(this.t, this.sideEffects);
        traverser.loops = this.loops;
        traverser.path.add(this.path);
        traverser.future = this.future;
        return traverser;
    }

    @Override
    public PathTraverser<T> deflate() {
        if (this.t instanceof Vertex && !(this.t instanceof DetachedVertex)) {
            this.t = (T) DetachedVertex.detach((Vertex) this.t);
        } else if (this.t instanceof Edge && !(this.t instanceof DetachedEdge)) {
            this.t = (T) DetachedEdge.detach((Edge) this.t);
        } else if (this.t instanceof MetaProperty && !(this.t instanceof DetachedMetaProperty)) {
            this.t = (T) DetachedMetaProperty.detach((MetaProperty) this.t);
        } else if (this.t instanceof Property && !(this.t instanceof DetachedProperty)) {
            this.t = (T) DetachedProperty.detach((Property) this.t);
        }
        this.path = DetachedPath.detach(this.path);
        this.dropSideEffects();
        return this;
    }

    @Override
    public PathTraverser<T> inflate(final Vertex vertex, final Traversal traversal) {
        if (this.t instanceof DetachedVertex) {
            this.t = (T) ((DetachedVertex) this.t).attach(vertex);
        } else if (this.t instanceof DetachedEdge) {
            this.t = (T) ((DetachedEdge) this.t).attach(vertex);
        } else if (this.t instanceof DetachedMetaProperty) {
            this.t = (T) ((DetachedMetaProperty) this.t).attach(vertex);
        } else if (this.t instanceof DetachedProperty) {
            this.t = (T) ((DetachedProperty) this.t).attach(vertex);
        }
        this.sideEffects = traversal.sideEffects();
        return this;
    }

    public boolean equals(final Object object) {
        return (object instanceof PathTraverser) && this.t.equals(((PathTraverser) object).get()) && this.path.equals(((PathTraverser) object).getPath());
    }
}
