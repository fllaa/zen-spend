import type { BottomSheetModal } from '@gorhom/bottom-sheet';
import type React from 'react';
import { createContext, useCallback, useContext, useMemo, useRef, useState } from 'react';

interface ModalContextType {
  ref: React.RefObject<BottomSheetModal | null>;
  isOpen: boolean;
  child: React.ReactNode | null;
  open: (child: React.ReactNode) => void;
  close: () => void;
}

const ModalContext = createContext<ModalContextType | undefined>(undefined);

export const ModalProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const ref = useRef<BottomSheetModal>(null);
  const [isOpen, setIsOpen] = useState(false);
  const [child, setChild] = useState<React.ReactNode | null>(null);

  const open = useCallback((child: React.ReactNode) => {
    setChild(child);
    setIsOpen(true);
    ref.current?.present();
  }, []);

  const close = useCallback(() => {
    ref.current?.dismiss();
    setIsOpen(false);
    setChild(null);
  }, []);

  const value = useMemo(
    () => ({
      ref,
      isOpen,
      child,
      open,
      close,
    }),
    [isOpen, child, open, close],
  );

  return <ModalContext.Provider value={value}>{children}</ModalContext.Provider>;
};

export const useModal = () => {
  const context = useContext(ModalContext);
  if (!context) {
    throw new Error('useModal must be used within ModalProvider');
  }
  return context;
};
